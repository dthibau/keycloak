var express = require('express');
var session = require('express-session');
var Keycloak = require('keycloak-connect');
var cors = require('cors');

var app = express();

app.use(cors());

var memoryStore = new session.MemoryStore();

app.use(session({
  secret: 'some secret',
  resave: false,
  saveUninitialized: true,
  store: memoryStore
}));

var keycloak = new Keycloak({ store: memoryStore });

app.use(keycloak.middleware());

// Trace de chaque requête reçue, avec les claims utiles du jeton s'il y en a un
app.use(function (req, res, next) {
  var grant = req.kauth && req.kauth.grant;
  if (grant) {
    var token = grant.access_token.content;
    console.log(new Date().toISOString(), req.method, req.url,
      'user=' + token.preferred_username, 'scope=[' + token.scope + ']', 'aud=' + JSON.stringify(token.aud));
  } else if (req.headers.authorization) {
    // Un jeton a été envoyé mais keycloak-connect l'a rejeté silencieusement :
    // on rejoue la validation pour tracer la raison (audience, expiration, signature...)
    var raw = req.headers.authorization.replace(/^Bearer /i, '');
    return keycloak.grantManager.createGrant({ access_token: raw })
      .then(function () { next(); })
      .catch(function (err) {
        console.log(new Date().toISOString(), req.method, req.url, 'jeton présent mais rejeté :', err.message);
        next();
      });
  } else {
    console.log(new Date().toISOString(), req.method, req.url, 'user=anonymous (pas de jeton)');
  }
  next();
});

// Garde vérifiant la présence d'un scope dans le claim "scope" du jeton d'accès.
// keycloak.protect('xxx') ne connaît que les rôles, d'où cette fonction.
function requireScope(scope) {
  return function (token) {
    var scopes = (token.content.scope || '').split(' ');
    return scopes.includes(scope);
  };
}

app.get('/secured', keycloak.protect(requireScope('email')), function (req, res) {
  var token = req.kauth.grant.access_token.content;
  console.log('Accès /secured autorisé pour', token.preferred_username, '- scopes :', token.scope);
  res.setHeader('content-type', 'text/plain');
  res.send('Secret message!');
});

app.get('/public', function (req, res) {
  res.setHeader('content-type', 'text/plain');
  res.send('Public message!');
});

app.get('/', function (req, res) {
  res.send('<html><body><ul><li><a href="/public">Public endpoint</a></li><li><a href="/secured">Secured endpoint</a></li></ul></body></html>');
});

app.listen(3000, function () {
  console.log('Started at port 3000');
});
