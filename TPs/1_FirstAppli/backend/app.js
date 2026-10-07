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

// Trace de chaque requête reçue, avec l'utilisateur extrait du jeton s'il y en a un
app.use(function (req, res, next) {
  var grant = req.kauth && req.kauth.grant;
  var user = grant ? grant.access_token.content.preferred_username : 'anonymous';
  console.log(new Date().toISOString(), req.method, req.url, 'user=' + user);
  next();
});

app.get('/secured', keycloak.protect('realm:ROLE_USER'), function (req, res) {
  var token = req.kauth.grant.access_token.content;
  console.log('Accès /secured par', token.preferred_username, 'rôles realm:', token.realm_access.roles);
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
