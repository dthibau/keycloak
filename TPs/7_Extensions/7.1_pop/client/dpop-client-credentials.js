import fetch from 'node-fetch';
import { generateKeyPair, exportJWK, SignJWT, calculateJwkThumbprint } from 'jose';
import crypto from 'crypto';

// === Configuration ===
const TOKEN_URL = 'http://localhost:8080/realms/formation/protocol/openid-connect/token';
const API_URL = 'http://localhost:3001/secure';
const CLIENT_ID = 'dpop-client';
const CLIENT_SECRET = 'secret';

async function main() {
  // 1. Génère une paire de clés EC pour DPoP
  const { privateKey, publicKey } = await generateKeyPair('ES256');
  const jwk = await exportJWK(publicKey);
  jwk.alg = 'ES256';
  jwk.use = 'sig';

  // 2. Calcule le thumbprint
  const jkt = await calculateJwkThumbprint(jwk);
  console.log('JWK thumbprint (jkt):', jkt);

  // 3. Fonction utilitaire pour générer un JWT DPoP
  async function createDpopProof(htm, htu) {
    const now = Math.floor(Date.now() / 1000);
    const jwt = await new SignJWT({
      htm,
      htu,
      jti: crypto.randomUUID(),
      iat: now,
    })
      .setProtectedHeader({ alg: 'ES256', typ: 'dpop+jwt', jwk })
      .sign(privateKey);

    const payload = { htm, htu, jti: '...', iat: now };
    console.log(`🧾 DPoP proof for ${htm} ${htu}:`, payload);
    return jwt;
  }

  // 4. DPoP pour l'obtention du token (lié au token endpoint)
  const tokenDpop = await createDpopProof('POST', TOKEN_URL);

  const params = new URLSearchParams();
  params.append('grant_type', 'client_credentials');
  params.append('client_id', CLIENT_ID);
  params.append('client_secret', CLIENT_SECRET);

  const tokenResponse = await fetch(TOKEN_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      'DPoP': tokenDpop,
    },
    body: params,
  });

  const tokenJson = await tokenResponse.json();
  console.log('Token response:', tokenJson);

  if (!tokenJson.access_token) {
    console.error('❌ Token not received.');
    return;
  }

  const accessToken = tokenJson.access_token;
  console.log('✅ Received access token.');

  
  // Décodage simple du JWT (payload)
  try {
    const [, payload] = accessToken.split('.');
    const decoded = JSON.parse(Buffer.from(payload, 'base64url').toString());
    console.log('Access token payload:', decoded);

    if (decoded.cnf && decoded.cnf.jkt) {
      console.log('DPoP confirmed with jkt:', decoded.cnf.jkt);
    } else {
      console.warn('DPoP proof NOT found in access token payload (missing cnf.jkt)');
    }
  } catch (e) {
    console.error('Failed to decode access token payload:', e);
  }

  // 5. Nouvelle preuve DPoP pour accéder à l'API
  const apiDpop = await createDpopProof('POST', API_URL);

  const apiResponse = await fetch(API_URL, {
    method: 'POST',
    headers: {
      'Authorization': `DPoP ${accessToken}`,
      'DPoP': apiDpop,
    },
  });

  const apiText = await apiResponse.text();
  console.log(`🔐 API response (${apiResponse.status}):`, apiText);

  const badApiResponse = await fetch(API_URL, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${accessToken}`,
    },
  });

  const badApiText = await badApiResponse.text();
  console.log(`🔐 API response (${badApiResponse.status}):`, badApiText);
}

main().catch(console.error);
