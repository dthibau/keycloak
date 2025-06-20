import express from 'express';
import fetch from 'node-fetch';
import { jwtVerify, createRemoteJWKSet } from 'jose';

const app = express();
const PORT = 3001;

// Configuration Keycloak
const KEYCLOAK_JWKS_URI = 'http://localhost:8080/realms/formation/protocol/openid-connect/certs';
const JWKS = createRemoteJWKSet(new URL(KEYCLOAK_JWKS_URI));

app.post('/secure', async (req, res) => {
  const auth = req.headers.authorization;
  const dpopProof = req.headers.dpop;

  if (!auth || !dpopProof) {
    return res.status(401).send('Missing Authorization or DPoP headers');
  }

  const token = auth.replace('DPoP ', '').trim();

  try {
    // 1. Vérifie le token d'accès avec les clés publiques de Keycloak
    const { payload, protectedHeader } = await jwtVerify(token, JWKS, {
      algorithms: ['RS256']
    });

    // 2. Vérifie que le token contient une preuve de possession (`cnf`)
    if (!payload.cnf || !payload.cnf.jkt) {
      return res.status(401).send('Token is not bound to a DPoP key');
    }

    // 3. Vérifie la preuve DPoP
    const dpopJwt = await jwtVerify(dpopProof, async (header, _token) => {
      // La clé est dans le header DPoP JWT (clé publique du client)
      if (!header.jwk) throw new Error('Missing JWK in DPoP header');
      return await importJWK(header.jwk, header.alg);
    });

    const dpopThumbprint = await calculateThumbprint(dpopJwt.protectedHeader.jwk);
    if (payload.cnf.jkt !== dpopThumbprint) {
      return res.status(401).send('DPoP proof does not match token binding');
    }

    // 4. Vérifie que `htu` et `htm` correspondent à la requête actuelle
    const expectedHTU = `${req.protocol}://${req.headers.host}${req.originalUrl}`;
    const expectedHTM = req.method;

    if (
      dpopJwt.payload.htu !== expectedHTU ||
      dpopJwt.payload.htm !== expectedHTM
    ) {
      return res.status(401).send('DPoP proof htu/htm mismatch');
    }

    return res.status(200).send(`Hello ${payload.preferred_username}, your token is valid and bound with DPoP!`);
  } catch (err) {
    console.error('DPoP verification error:', err);
    return res.status(401).send('Invalid token or DPoP proof');
  }
});

app.listen(PORT, () => {
  console.log(`🔐 API DPoP sécurisée disponible sur http://localhost:${PORT}/secure`);
});

// === Utils ===

import { importJWK, calculateJwkThumbprint } from 'jose';

async function calculateThumbprint(jwk) {
  return await calculateJwkThumbprint(jwk, 'sha256');
}
