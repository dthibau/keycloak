# Atelier 5.4 – Intégration Python

- `app.py` : application web Flask, login OpenID Connect (authorization code + PKCE) avec Authlib, appel de l'API, logout RP-initiated.
- `api.py` : API FastAPI qui valide les jetons d'accès Keycloak avec PyJWT (JWKS, issuer, audience `python-api`).

```bash
python -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt

export KC_CLIENT_SECRET=<secret du client flask-app>
uvicorn api:app --port 8002          # terminal 1
flask --app app run --port 5000      # terminal 2
```

Le client `flask-app` (confidentiel, Standard flow, redirect URI `http://localhost:5000/auth`,
post logout redirect URI `http://localhost:5000/`) doit exister dans le realm `formation`.
L'appel à l'API renvoie 401 tant que le mapper Audience `python-api` n'est pas ajouté au client.
