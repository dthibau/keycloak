#!/usr/bin/env bash
# Demande un jeton (password grant) et affiche le code HTTP et le nœud qui a répondu
# Usage : ./scripts/token.sh [utilisateur] [mot_de_passe]
U=${1:-alice}; P=${2:-alice}
curl -s -o /dev/null -D - \
  -d grant_type=password -d client_id=load-test -d username="$U" -d password="$P" \
  http://localhost:8080/realms/cluster-lab/protocol/openid-connect/token \
  | grep -i -E '^HTTP|^x-keycloak-node' | tr -d '\r' | tr '\n' ' '
echo
