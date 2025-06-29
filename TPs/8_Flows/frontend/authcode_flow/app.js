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

app.post('/logout', function(req, res) {
     console.log('Déconnexion ' + req);
     console.log('Déconnexion ', req.headers);
     console.log('Corps ', req.body);
   
    res.send('<html><body><H1>Vous êtes déconnecté</H1></body></html>');
});


app.listen(8000);
