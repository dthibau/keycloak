#!/usr/bin/env bash
# Pic de connexions : N utilisateurs différents (user0001..userN) se connectent,
# P à la fois, via l'accès public. Chaque login d'un utilisateur absent du cache
# lit l'utilisateur, ses credentials et ses rôles en base, puis écrit sa session.
# Prérequis : ./scripts/create-users.sh
# Usage : ./scripts/login-storm.sh [N=1000] [P=40]
N=${1:-1000}; P=${2:-40}
URL=http://localhost:8080/realms/cluster-lab/protocol/openid-connect/token
seq -f "%04g" 1 "$N" | xargs -P "$P" -I{} curl -s -o /dev/null -w '%{http_code} %{time_total}\n' \
  -d grant_type=password -d client_id=load-test -d username=user{} -d password=password "$URL" \
  | sort -k2 -n \
  | awk '{c[$1]++; t[NR]=$2; s+=$2} END {for(k in c) printf "HTTP %s : %d\n",k,c[k];
         printf "temps moyen %.3fs, médian %.3fs, p95 %.3fs, max %.3fs\n", s/NR, t[int(NR/2)+1], t[int(NR*0.95)], t[NR]}'
