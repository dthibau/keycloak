# Stack 11_Cluster – ateliers 11 à 14 (J4 et J5)

| Service    | URL                         | Rôle                                   |
|------------|-----------------------------|----------------------------------------|
| HAProxy    | http://localhost:8080       | accès public (realms, applications)    |
| HAProxy    | http://localhost:8081/admin | console d'administration (admin/admin) |
| HAProxy    | http://localhost:8404       | statistiques, /metrics                 |
| kc1/kc2/kc3| http://localhost:9001..9003 | interface de management (health, metrics) |
| Mailpit    | http://localhost:8025       | SMTP authentifié keycloak / S3cr3t     |
| Prometheus | http://localhost:9090       | profil `monitoring`                    |
| Grafana    | http://localhost:3000       | profil `monitoring` (admin/admin)      |

Realm importé au premier démarrage de kc1 : `cluster-lab`
(utilisateurs alice/alice et bob/bob, client public `load-test` avec direct access grants).

Scripts :
- `scripts/token.sh [user] [password]` : un jeton, code HTTP et nœud qui a répondu
- `scripts/watch.sh` : boucle de demandes de jetons (continuité de service)
- `scripts/load.sh [N] [P]` : N demandes de jeton, P en parallèle (charge surtout CPU)
- `scripts/create-users.sh [N]` : crée N utilisateurs user0001..userN (mot de passe `password`)
- `scripts/login-storm.sh [N] [P]` : pic de connexions de N utilisateurs différents, P en parallèle
- `scripts/pool.sh [port]` : suivi en continu du pool de connexions d'un nœud
- `monitoring/download-dashboards.sh` : dashboards Grafana officiels de Keycloak

Version de Keycloak : variable `KC_VERSION` du fichier `.env` (26.7.4 au départ, 26.8.0 après l'atelier 13.2).

Prérequis : arrêter les stacks de l'atelier 10 et la stack persistante (port 8080).

Remise à zéro complète : `docker compose --profile scale --profile monitoring down -v`
