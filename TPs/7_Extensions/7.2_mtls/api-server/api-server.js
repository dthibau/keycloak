const https = require('https');
const express = require('express');
const fs = require('fs');
const jwt = require('jsonwebtoken'); // Pour décoder le JWT
const crypto = require('crypto');

const app = express();

// Charger les certificats serveur et CA
const serverOptions = {
  key: fs.readFileSync('./api-server.key'),
  cert: fs.readFileSync('./api-server.crt'),
  ca: fs.readFileSync('./ca-from-keycloak.crt'),
  requestCert: true,
  rejectUnauthorized: true // On rejette les clients sans cert valide
};

// Middleware de vérification du token et du certificat client
app.use((req, res, next) => {
    if (req.path === '/ping') return next();
  try {
    const clientCert = req.socket.getPeerCertificate(true);
    if (!clientCert || !clientCert.raw) {
      console.error('❌ Aucun certificat client fourni ou invalide');
      return res.status(401).send('Client certificate missing or invalid');
    }

    console.log('✅ Certificat client reçu :', clientCert.subject);

    const auth = req.headers.authorization;
    if (!auth?.startsWith('Bearer ')) {
      console.warn('⚠️ Token manquant');
      return res.status(401).send('Missing Authorization header');
    }

    const token = auth.slice('Bearer '.length);
    const payload = jwt.decode(token, { complete: true });
    const expectedThumbprint = payload?.payload?.cnf?.['x5t#S256'];

    if (!expectedThumbprint) {
      console.warn('⚠️ Token sans champ cnf');
      return res.status(400).send('No cnf.x5t#S256 in token');
    }

    const actualThumbprint = crypto
      .createHash('sha256')
      .update(clientCert.raw)
      .digest('base64url');

    if (actualThumbprint !== expectedThumbprint) {
      console.error('❌ Mauvais certificat (thumbprint ne correspond pas)');
      return res.status(401).send('Certificate mismatch');
    }

    console.log('✅ Preuve de possession validée');
    req.user = payload.payload;
    next();
  } catch (e) {
    console.error('💥 Erreur dans middleware mTLS', e);
    return res.status(500).send('Server error');
  }
});

app.get('/resource', (req, res) => {
  res.json({
    message: 'Access granted via mTLS proof-of-possession!',
    client: req.user
  });
});
app.get('/ping', (req, res) => {
  res.send('pong');
});

https.createServer(serverOptions, app).listen(9443, () => {
  console.log('🔐 mTLS API server running on https://localhost:9443');
});
