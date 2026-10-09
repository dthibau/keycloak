# Atelier 10.2 – Configuration de production (mono-nœud)

1. `certs/gen-certs.sh` : génère une CA de formation et le certificat serveur
2. `docker compose up -d` : PostgreSQL, pgAdmin, Keycloak en HTTPS direct (port 8443)
3. `docker compose -f docker-compose.yml -f docker-compose.proxy.yml up -d` :
   ajout de Nginx (TLS terminé au proxy, https://localhost et https://admin.localhost)

Appels HTTPS en ligne de commande : `curl --cacert certs/ca.crt ...`
Variable DB_PASSWORD : fichier `.env`
