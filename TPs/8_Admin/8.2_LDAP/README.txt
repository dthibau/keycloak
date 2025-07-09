# Keycloak + OpenLDAP Demo Stack

Ce projet contient une stack prête à l'emploi pour tester la synchronisation entre Keycloak et un serveur LDAP avec des utilisateurs simulés.

## 🔧 Lancer le projet

```bash
docker-compose up -d
```

## 🌍 Accès

- Keycloak : http://localhost:8080  
  - Identifiants admin : `admin / admin`
- phpLDAPadmin : https://localhost:6443  
  - Login : `cn=admin,dc=cyberlab,dc=local`  
  - Mot de passe : `admin`

## 📁 Fichier LDAP injecté

Le fichier `ldap/seed.ldif` contient 10 utilisateurs répartis dans les groupes suivants :

- HMN_Biologistes
- HMN_Cliniciens
- HMN_MedecineDuTravail

Tu peux l’adapter pour étendre ou modifier les utilisateurs/groupes.

## ✅ Étapes pour la synchronisation LDAP dans Keycloak

1. Aller dans l'admin Keycloak > User Federation > LDAP > Add provider.
2. Renseigner :
   - Connection URL : `ldap://ldap-server:389`
   - Bind DN : `cn=admin,dc=cyberlab,dc=local`
   - Bind Credentials : `admin`
   - Users DN : `ou=users,dc=cyberlab,dc=local`
   - Groups DN : `ou=groups,dc=cyberlab,dc=local`
   - Vendor : `Other`
3. Sauvegarder et synchroniser les utilisateurs.
