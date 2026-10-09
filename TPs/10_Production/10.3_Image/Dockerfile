ARG KC_VERSION=26.8.0

# ---------- Étape 1 : build ----------
FROM quay.io/keycloak/keycloak:${KC_VERSION} AS builder

# Options de build, reprises de l'atelier 10.2
ENV KC_DB=postgres \
    KC_HEALTH_ENABLED=true \
    KC_METRICS_ENABLED=true

WORKDIR /opt/keycloak
RUN /opt/keycloak/bin/kc.sh build

# ---------- Étape 2 : image finale ----------
FROM quay.io/keycloak/keycloak:${KC_VERSION}
COPY --from=builder /opt/keycloak/ /opt/keycloak/

# Options d'exécution par défaut, sans secret
COPY conf/keycloak.conf /opt/keycloak/conf/keycloak.conf

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
CMD ["start", "--optimized"]
