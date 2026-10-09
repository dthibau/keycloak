var express = require('express');
var app = express();
var stringReplace = require('string-replace-middleware');

var KC_URL = process.env.KC_URL || "http://localhost:8080";
var SERVICE_URL = process.env.SERVICE_URL || "http://localhost:9000/protected";

app.use(stringReplace({
   'KC_URL': KC_URL,
   'SERVICE_URL': SERVICE_URL
}));
// Adaptateur keycloak-js installé par npm, exposé sous /keycloak-js
app.use('/keycloak-js', express.static('node_modules/keycloak-js'));
app.use(express.static('.'))

app.get('/', function(req, res) {
    res.render('index.html');
});

app.listen(8000, function () {
  console.log('Started at port 8000');
});
