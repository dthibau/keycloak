#!/usr/bin/env bash
# Boucle de requêtes : permet de vérifier la continuité de service (Ctrl+C pour arrêter)
while true; do
  printf '%s ' "$(date +%T)"
  "$(dirname "$0")/token.sh" alice alice
  sleep 0.5
done
