# API REST Spring Boot protégée par les Authorization Services de Keycloak

Atelier 6.2 : les autorisations (ressources, policies, permissions) sont définies dans Keycloak,
onglet *Authorization* du client `app-authz-rest-springboot`, et appliquées par le **policy enforcer**
Keycloak intégré à l'application. Le code ne contient aucune règle d'autorisation.

Prérequis : JDK 21, Keycloak démarré sur http://localhost:8080 avec le realm
`spring-boot-quickstart` importé depuis `config/quickstart-realm-simple.json`.

## Fichiers à lire

| Fichier | Rôle |
|---|---|
| `pom.xml` | Spring Boot 4, `spring-boot-starter-oauth2-resource-server` (validation du JWT) et `keycloak-policy-enforcer` |
| `src/main/resources/application.yml` | port 8180 et `issuer-uri` du realm (Spring récupère le JWKS tout seul) |
| `src/main/resources/policy-enforcer.json` | configuration du policy enforcer : realm, URL Keycloak, client et secret |
| `SecurityConfiguration.java` | chaîne de filtres : jeton JWT obligatoire (401 sinon), puis policy enforcer (403 si pas de permission) |
| `ApplicationController.java` | les endpoints `/api/resourcea`, `/api/resourceb`, `/api/premium`, `/api/admin`, sans annotation de sécurité |

## Démarrage

```bash
./mvnw spring-boot:run
```

## Exports de realm

- `config/quickstart-realm-simple.json` : realm, rôles, utilisateurs (`alice`/`alice`, `jdoe`/`jdoe`), client
  confidentiel avec les Authorization Services activés et les ressources *Default Resource* (`/api/{resource}`)
  et *Premium Resource* (`/api/premium`). Aucune policy ni permission : à créer dans la console (c'est l'exercice).
- `config/quickstart-realm.json` : même chose avec les policies et permissions déjà créées (solution).

Le client a l'option *Remote resource management* activée : le policy enforcer s'en sert pour retrouver,
à la première requête sur un chemin, la ressource Keycloak dont l'URI correspond (`lazy-load-paths`).

## Comment ça marche

1. Spring Security valide le jeton d'accès (signature via le JWKS du realm, issuer, expiration).
2. Le policy enforcer appelle le endpoint token de Keycloak avec le grant
   `urn:ietf:params:oauth:grant-type:uma-ticket`, en présentant le jeton de l'utilisateur et le secret du client,
   pour obtenir les permissions sur la ressource correspondant au chemin appelé.
3. Permission accordée : la requête atteint le contrôleur. Refusée : réponse 403.

Les traces `DEBUG` de `org.keycloak` (activées dans `application.yml`) montrent ces échanges.
