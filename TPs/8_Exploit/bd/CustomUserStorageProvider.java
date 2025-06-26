public class CustomUserStorageProvider implements UserStorageProvider,
        UserLookupProvider, CredentialInputValidator {

    private final KeycloakSession session;
    private final ComponentModel model;

    public CustomUserStorageProvider(KeycloakSession session, ComponentModel model) {
        this.session = session;
        this.model = model;
    }

    @Override
    public void close() {
        // Libérer les ressources si besoin
    }

    // Rechercher un utilisateur par username
    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        // Exemple : appel à ta base perso
        UserEntity user = findUserInCustomDB(username);
        if (user == null) return null;

        return new CustomUserAdapter(session, realm, model, user);
    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        String externalId = StorageId.externalId(id);
        return getUserByUsername(realm, externalId);
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        // Même logique que getUserByUsername
        return null;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        if (!(input instanceof UserCredentialModel)) return false;

        String username = user.getUsername();
        String password = input.getChallengeResponse();

        return checkPassword(username, password); // à implémenter avec ta logique
    }

    // Tes méthodes custom vers ta BDD :
    private UserEntity findUserInCustomDB(String username) {
        // Appel SQL ou JPA
        return null;
    }

    private boolean checkPassword(String username, String password) {
        // Vérification de mot de passe (hash etc.)
        return true;
    }
}
