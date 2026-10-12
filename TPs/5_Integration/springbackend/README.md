# Atelier 5.2 – Application web Spring MVC sécurisée par Keycloak

Spring Boot 4, Spring Security OAuth2 client : login OpenID Connect (authorization code + PKCE),
déconnexion RP-initiated et back-channel logout. Projet fourni complet, à lire puis à exécuter.

Fichiers à regarder :
- `src/main/resources/application.yml` : provider `keycloak` (issuer-uri), registration `springapp` (client-id, secret, redirect-uri, scope).
- `src/main/java/org/formation/SpringbackendApplication.java` : chaîne de sécurité (`oauth2Login`, `oidcLogout`, `logout`) et endpoints `/oidc-principal`, `/access-token`.

Démarrage (Java 17 ou plus, Keycloak sur http://localhost:8080, client `springapp` déclaré dans le realm `formation`) :

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--spring.security.oauth2.client.registration.springapp.client-secret=<secret>
```

Puis http://localhost:8001/oidc-principal, http://localhost:8001/access-token, http://localhost:8001/logout.
