#!/usr/bin/env bash
# Télécharge les dashboards Grafana officiels de Keycloak et les adapte à la datasource locale
set -euo pipefail
cd "$(dirname "$0")/grafana/dashboards"
BASE=https://raw.githubusercontent.com/keycloak/keycloak-grafana-dashboard/main/dashboards
for d in keycloak-troubleshooting-dashboard keycloak-capacity-planning-dashboard; do
  curl -fsSL "$BASE/$d.json" | sed 's/\${DS_PROMETHEUS}/prometheus/g' > "$d.json"
  echo "OK $d.json"
done
