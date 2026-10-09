import express from 'express';
import cors from 'cors';
import { createRemoteJWKSet, jwtVerify } from 'jose';

// jose s'appuie sur les API web du runtime (fetch, Headers, WebCrypto) : Node.js 20 ou plus est requis
var nodeMajor = Number(process.versions.node.split('.')[0]);
if (nodeMajor < 20) {
  console.error('Node.js 20 ou plus est requis, version actuelle : ' + process.version + ' (avec nvm : nvm use 22)');
  process.exit(1);
}

// Configuration : l'issuer est l'URL du realm, l'audience attendue est le client "backend"
var ISSUER = process.env.KC_ISSUER || 'http://localhost:8080/realms/formation';
var AUDIENCE = process.env.KC_AUDIENCE || 'backend';

// Clés publiques du realm, récupérées sur le endpoint JWKS (annoncé par .well-known/openid-configuration)
// et mises en cache par jose, avec rechargement automatique en cas de rotation des clés.
var JWKS = createRemoteJWKSet(new URL(ISSUER + '/protocol/openid-connect/certs'));

// Middleware : exige un jeton d'accès Bearer valide (signature, issuer, expiration, audience)
async function requireToken(req, res, next) {
  var header = req.headers.authorization || '';
  if (!header.startsWith('Bearer ')) {
    console.log(new Date().toISOString(), req.method, req.url, 'refusé : pas de jeton');
    res.set('WWW-Authenticate', 'Bearer realm="formation"');
    return res.status(401).send('Missing bearer token');
  }
  try {
    var { payload } = await jwtVerify(header.substring(7), JWKS, {
      issuer: ISSUER,
      audience: AUDIENCE
    });
    req.token = payload;
    console.log(new Date().toISOString(), req.method, req.url,
      'user=' + payload.preferred_username, 'scope=[' + payload.scope + ']', 'aud=' + JSON.stringify(payload.aud));
    next();
  } catch (err) {
    console.log(new Date().toISOString(), req.method, req.url, 'jeton rejeté :', err.code || err.name, '-', err.message);
    res.set('WWW-Authenticate', 'Bearer realm="formation", error="invalid_token"');
    res.status(401).send('Invalid token: ' + err.message);
  }
}

var app = express();

app.use(cors());

app.get('/protected', requireToken, function (req, res) {
  res.setHeader('content-type', 'text/plain');
  res.send('Access granted to protected resource for ' + req.token.preferred_username);
});

app.listen(9000, function () {
  console.log('Started at port 9000 - issuer ' + ISSUER + ', audience ' + AUDIENCE);
});
