var express = require('express');
var app = express();
var stringReplace = require('string-replace-middleware');

var KC_URL = process.env.KC_URL || "http://localhost:8080/";
var SERVICE_URL = process.env.SERVICE_URL || "http://localhost:3000/secured";

app.use(stringReplace({
   'SERVICE_URL': SERVICE_URL,
   'KC_URL': KC_URL
}));
app.use(express.static('.'))


app.get('/', function(req, res) {
    res.render('index.html');
});

// Back-channel logout : Keycloak envoie un POST application/x-www-form-urlencoded
// contenant un paramètre logout_token (JWT signé)
app.post('/logout', express.urlencoded({ extended: false }), function(req, res) {
    console.log('--- Back-channel logout reçu de ' + req.ip);
    console.log('En-têtes :', req.headers);
    console.log('Corps :', req.body);

    var logoutToken = req.body && req.body.logout_token;
    if (logoutToken) {
        var parts = logoutToken.split('.');
        var header = JSON.parse(Buffer.from(parts[0], 'base64url').toString());
        var claims = JSON.parse(Buffer.from(parts[1], 'base64url').toString());
        console.log('logout_token header :', header);
        console.log('logout_token claims :', claims);
        console.log('Session Keycloak à fermer (sid) :', claims.sid, '- utilisateur (sub) :', claims.sub);
    } else {
        console.log('Aucun logout_token dans la requête');
    }

    // Une vraie application vérifierait la signature du jeton et invaliderait la session locale
    res.status(200).send('<html><body><H1>Vous êtes déconnecté</H1></body></html>');
});


app.listen(8000);
