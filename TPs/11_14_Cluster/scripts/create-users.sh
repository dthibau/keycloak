#!/usr/bin/env bash
# Crée N utilisateurs user0001..userN (mot de passe "password") dans le realm cluster-lab,
# par import partiel, en lots de 100. Les utilisateurs existants sont ignorés.
# Durée : quelques minutes, Keycloak hache chaque mot de passe.
# Usage : ./scripts/create-users.sh [N=1000]
set -euo pipefail
N=${1:-1000}; BATCH=100
ADMIN=http://localhost:8081
token() {
  curl -s -d grant_type=password -d client_id=admin-cli -d username=admin -d password=admin \
    "$ADMIN/realms/master/protocol/openid-connect/token" | jq -r .access_token
}
for ((start=1; start<=N; start+=BATCH)); do
  end=$(( start+BATCH-1 < N ? start+BATCH-1 : N ))
  jq -n --argjson s "$start" --argjson e "$end" '{
    ifResourceExists: "SKIP",
    users: [ range($s; $e+1) as $i
      | ("000" + ($i|tostring))[-4:] as $n
      | { username: ("user" + $n), enabled: true,
          email: ("user" + $n + "@example.com"), emailVerified: true,
          firstName: "User", lastName: $n,
          credentials: [ { type: "password", value: "password", temporary: false } ],
          realmRoles: [ "default-roles-cluster-lab" ] } ]
  }' > /tmp/users-batch.json
  code=$(curl -s -o /dev/null -w '%{http_code}' -X POST \
    -H "Authorization: Bearer $(token)" -H "Content-Type: application/json" \
    --data @/tmp/users-batch.json "$ADMIN/admin/realms/cluster-lab/partialImport")
  echo "utilisateurs $start à $end : HTTP $code"
done
