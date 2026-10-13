package org.keycloak.quickstart.springboot.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Les chemins /api/... ne portent aucune annotation de sécurité : les autorisations sont
 * définies dans Keycloak (onglet Authorization du client) et appliquées par le policy enforcer.
 */
@RestController
public class ApplicationController {

    @GetMapping("/api/resourcea")
    public String handleResourceA(@AuthenticationPrincipal Jwt jwt) {
        return createResponse(jwt);
    }

    @GetMapping("/api/resourceb")
    public String handleResourceB(@AuthenticationPrincipal Jwt jwt) {
        return createResponse(jwt);
    }

    @GetMapping("/api/premium")
    public String handlePremiumResource(@AuthenticationPrincipal Jwt jwt) {
        return createResponse(jwt);
    }

    @GetMapping("/api/admin")
    public String handleAdminResource(@AuthenticationPrincipal Jwt jwt) {
        return createResponse(jwt);
    }

    private String createResponse(Jwt jwt) {
        return "Access Granted (" + jwt.getClaimAsString("preferred_username") + ")";
    }
}
