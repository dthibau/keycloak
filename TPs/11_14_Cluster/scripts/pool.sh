#!/usr/bin/env bash
# Affiche le pool de connexions d'un nœud toutes les 0,5 s (Ctrl+C pour arrêter)
# Usage : ./scripts/pool.sh [port de management=9001]
PORT=${1:-9001}
while true; do
  printf '%s ' "$(date +%T)"
  curl -s "localhost:$PORT/metrics" | grep -E '^agroal_(active|available|awaiting|max_used)_count' \
    | sed -E 's/agroal_([a-z_]+)_count\{[^}]*\} ([0-9.]+)/\1=\2/' | tr '\n' ' '
  echo
  sleep 0.5
done
