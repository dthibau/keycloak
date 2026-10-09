# Atelier 10.3 – Image optimisée

Reprend la configuration de 10.2 (PostgreSQL, Nginx, hostname) dans une image construite
avec kc.sh build et démarrée avec start --optimized.
Le reverse proxy et les certificats sont ceux de ../10.2_Production (générer les certificats avant).

    docker compose build
    docker compose up -d

Le répertoire import/ peut recevoir un export de realm (option --import-realm).
