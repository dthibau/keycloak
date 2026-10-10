ARG KC_VERSION=26.7.4
FROM quay.io/keycloak/keycloak:${KC_VERSION}

# Options figées dans l'image par kc.sh build (même principe qu'en 10.3).
# Elles restent aussi présentes à l'exécution : un démarrage avec "start"
# ne détecte donc aucun changement et ne reconstruit pas le serveur.
ENV KC_DB=postgres \
    KC_HEALTH_ENABLED=true \
    KC_METRICS_ENABLED=true \
    KC_FEATURES=user-event-metrics \
    KC_EVENT_METRICS_USER_ENABLED=true \
    KC_HTTP_METRICS_HISTOGRAMS_ENABLED=true \
    KC_VAULT=file

RUN /opt/keycloak/bin/kc.sh build

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
