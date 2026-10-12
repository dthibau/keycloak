"""Application web Flask sécurisée par Keycloak (OpenID Connect, authorization code + PKCE).

Démarrage :
    export KC_CLIENT_SECRET=<secret du client flask-app>
    flask --app app run --port 5000
"""
import os

import requests
from authlib.integrations.flask_client import OAuth
from flask import Flask, redirect, session, url_for

ISSUER = os.environ.get("KC_ISSUER", "http://localhost:8080/realms/formation")
API_URL = os.environ.get("API_URL", "http://localhost:8002/api/hello")

app = Flask(__name__)
app.secret_key = "change-me"  # clé de signature du cookie de session, à remplacer en production

oauth = OAuth(app)
oauth.register(
    "keycloak",
    client_id="flask-app",
    # Secret du client flask-app (Clients → flask-app → Credentials), ici lu dans l'environnement
    client_secret=os.environ.get("KC_CLIENT_SECRET", "<secret>"),
    # Authlib lit les endpoints (authorization, token, userinfo, end_session) et les clés dans la discovery
    server_metadata_url=ISSUER + "/.well-known/openid-configuration",
    client_kwargs={"scope": "openid profile email", "code_challenge_method": "S256"},
)


@app.route("/")
def index():
    user = session.get("user")
    if not user:
        return '<a href="/login">Se connecter</a>'
    return (
        f'Bonjour {user["preferred_username"]} - <a href="/api">API</a> - '
        '<a href="/token">Jeton</a> - <a href="/logout">Logout</a>'
    )


@app.route("/login")
def login():
    # Redirection vers Keycloak : Authlib génère state, nonce, code_verifier et code_challenge
    return oauth.keycloak.authorize_redirect(url_for("auth", _external=True))


@app.route("/auth")
def auth():
    # Retour de Keycloak : vérification du state, échange du code (avec code_verifier et secret),
    # validation de la signature et du nonce du jeton d'identification
    token = oauth.keycloak.authorize_access_token()
    session["user"] = {k: token["userinfo"].get(k) for k in ("sub", "preferred_username", "name", "email")}
    session["access_token"] = token["access_token"]
    session["id_token"] = token["id_token"]
    return redirect("/")


@app.route("/token")
def token():
    # Claims du jeton d'accès, décodés sans vérification (affichage pédagogique uniquement)
    import base64
    import json

    payload = session["access_token"].split(".")[1]
    payload += "=" * (-len(payload) % 4)
    claims = json.loads(base64.urlsafe_b64decode(payload))
    return f"<pre>{json.dumps(claims, indent=2)}</pre>"


@app.route("/api")
def api():
    # Appel de l'API protégée avec le jeton d'accès obtenu au login
    r = requests.get(API_URL, headers={"Authorization": "Bearer " + session["access_token"]})
    return f"{r.status_code} : {r.text}"


@app.route("/logout")
def logout():
    # RP-initiated logout : session locale effacée, puis redirection vers le end session endpoint de Keycloak
    id_token = session.get("id_token")
    session.clear()
    end = oauth.keycloak.load_server_metadata()["end_session_endpoint"]
    return redirect(f"{end}?id_token_hint={id_token}&post_logout_redirect_uri=http://localhost:5000/")
