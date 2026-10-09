# Atelier 10.1 – Options de démarrage

Pas de docker-compose : on lance le conteneur avec `docker run` pour voir l'effet de chaque option.
Le répertoire `conf/` contient :
- `keycloak.conf` : configuration principale, montée dans /opt/keycloak/conf/keycloak.conf
- `debug.conf` : configuration alternative, utilisée avec --config-file

Image : quay.io/keycloak/keycloak:26.8.0
Ports : 8080 (HTTP), 9000 (management)
