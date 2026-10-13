package org.keycloak.quickstart.springboot;

import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.keycloak.adapters.authorization.integration.jakarta.ServletPolicyEnforcerFilter;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.keycloak.util.JsonSerialization;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Sécurité de l'API en deux étapes :
 * 1. Spring Security (resource server) exige un jeton d'accès JWT valide : signature, issuer, expiration.
 *    Sans jeton ou avec un jeton invalide : 401.
 * 2. Le policy enforcer Keycloak demande au serveur d'autorisation (endpoint token, grant UMA)
 *    si le porteur du jeton a une permission sur la ressource correspondant au chemin appelé.
 *    Sans permission : 403.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            .addFilterAfter(policyEnforcerFilter(), BearerTokenAuthenticationFilter.class);
        return http.build();
    }

    /**
     * La configuration du policy enforcer (realm, URL de Keycloak, client et son secret)
     * est lue dans src/main/resources/policy-enforcer.json.
     * Le filtre n'est appliqué qu'aux requêtes portant un jeton : sans jeton, c'est Spring Security
     * qui répond 401 (et non 403).
     */
    private OncePerRequestFilter policyEnforcerFilter() {
        PolicyEnforcerConfig config;
        try (InputStream in = getClass().getResourceAsStream("/policy-enforcer.json")) {
            config = JsonSerialization.readValue(in, PolicyEnforcerConfig.class);
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de lire policy-enforcer.json", e);
        }
        ServletPolicyEnforcerFilter enforcer = new ServletPolicyEnforcerFilter(request -> config);

        return new OncePerRequestFilter() {
            @Override
            protected boolean shouldNotFilter(HttpServletRequest request) {
                return request.getHeader(HttpHeaders.AUTHORIZATION) == null
                        || request.getDispatcherType() == DispatcherType.ERROR;
            }

            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain) throws ServletException, IOException {
                enforcer.doFilter(request, response, chain);
            }
        };
    }
}
