package org.formation.cibabackend;

public class CibaRequest {
    private String login_hint;
    private String scope;
    private Boolean is_consent_required;
    private String binding_message;
    private String acr_values;

    public String getLogin_hint() {
        return login_hint;
    }

    public void setLogin_hint(String login_hint) {
        this.login_hint = login_hint;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Boolean getIs_consent_required() {
        return is_consent_required;
    }

    public void setIs_consent_required(Boolean is_consent_required) {
        this.is_consent_required = is_consent_required;
    }

    public String getBinding_message() {
        return binding_message;
    }

    public void setBinding_message(String binding_message) {
        this.binding_message = binding_message;
    }

    public String getAcr_values() {
        return acr_values;
    }

    public void setAcr_values(String acr_values) {
        this.acr_values = acr_values;
    }
}
