#!/usr/bin/env bash
# Génère une autorité de certification de formation et un certificat serveur
# valable pour localhost, admin.localhost et keycloak (nom du service Docker).
# Prérequis : openssl. À défaut :
#   docker run --rm -v "$PWD:/certs" -w /certs --entrypoint sh alpine/openssl gen-certs.sh
set -euo pipefail
cd "$(dirname "$0")"

openssl req -x509 -newkey rsa:2048 -nodes -days 365 \
  -subj "/CN=Formation Keycloak CA" \
  -keyout ca.key -out ca.crt

openssl req -newkey rsa:2048 -nodes \
  -subj "/CN=localhost" \
  -keyout server.key -out server.csr

cat > server.ext <<EXT
subjectAltName = DNS:localhost, DNS:admin.localhost, DNS:keycloak, IP:127.0.0.1
extendedKeyUsage = serverAuth
EXT

openssl x509 -req -in server.csr -CA ca.crt -CAkey ca.key -CAcreateserial \
  -days 365 -extfile server.ext -out server.crt

# lisibles par l'utilisateur du conteneur Keycloak (uid 1000)
chmod 644 server.key server.crt ca.crt
rm -f server.csr server.ext ca.srl
echo "Certificats générés : ca.crt, server.crt, server.key"
