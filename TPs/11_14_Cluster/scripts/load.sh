#!/usr/bin/env bash
# Génère de la charge : N demandes de jeton avec P requêtes en parallèle
# Usage : ./scripts/load.sh [N=500] [P=20]
N=${1:-500}; P=${2:-20}
URL=http://localhost:8080/realms/cluster-lab/protocol/openid-connect/token
seq 1 "$N" | xargs -P "$P" -I{} curl -s -o /dev/null -w '%{http_code} %{time_total}\n' \
  -d grant_type=password -d client_id=load-test -d username=alice -d password=alice "$URL" \
  | awk '{c[$1]++; t+=$2; if($2>m)m=$2} END {for(k in c) printf "HTTP %s : %d\n",k,c[k]; printf "temps moyen %.3fs, max %.3fs\n",t/NR,m}'
