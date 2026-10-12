"""API FastAPI protégée par les jetons d'accès de Keycloak.

Démarrage :
    uvicorn api:app --port 8002
"""
import os

import jwt
from fastapi import Depends, FastAPI, HTTPException
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer

ISSUER = os.environ.get("KC_ISSUER", "http://localhost:8080/realms/formation")
AUDIENCE = os.environ.get("KC_AUDIENCE", "python-api")

# Clés publiques du realm (endpoint JWKS), mises en cache par PyJWT
jwks = jwt.PyJWKClient(ISSUER + "/protocol/openid-connect/certs")

app = FastAPI()


def current_user(cred: HTTPAuthorizationCredentials = Depends(HTTPBearer())):
    """Valide le jeton Bearer : signature, expiration, issuer et audience."""
    try:
        key = jwks.get_signing_key_from_jwt(cred.credentials)
        return jwt.decode(cred.credentials, key.key, algorithms=["RS256"], audience=AUDIENCE, issuer=ISSUER)
    except jwt.PyJWTError as e:
        raise HTTPException(status_code=401, detail=str(e), headers={"WWW-Authenticate": "Bearer"})


@app.get("/api/hello")
def hello(claims: dict = Depends(current_user)):
    return {"user": claims["preferred_username"], "scope": claims.get("scope"), "aud": claims.get("aud")}
