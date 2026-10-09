package org.formation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpringbackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbackendApplication.class, args);
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
		return http
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/").permitAll()
				.requestMatchers("/oidc-principal").hasAuthority("OIDC_USER")
				.anyRequest().authenticated())
			// Login OpenID Connect avec le client "springapp" déclaré dans application.yml
			.oauth2Login(Customizer.withDefaults())
			// 5.2.1 : ajouter ici la déconnexion RP-initiated (logout + OidcClientInitiatedLogoutSuccessHandler)
			// puis le back-channel logout (oidcLogout)
			.csrf(csrf -> csrf.disable())
			.build();
	}

	@GetMapping("/")
	public String home() {
		return "Application Spring MVC sécurisée par Keycloak. Endpoints : /oidc-principal, /access-token, /logout";
	}

	// Utilisateur OpenID Connect authentifié : claims du jeton d'identification et autorités Spring
	@GetMapping("/oidc-principal")
	public OidcUser getOidcUserPrincipal(@AuthenticationPrincipal OidcUser principal) {
		return principal;
	}

	// Jeton d'accès obtenu lors du login, à propager vers un service protégé
	@GetMapping("/access-token")
	public String getAccessToken(@RegisteredOAuth2AuthorizedClient("springapp") OAuth2AuthorizedClient client) {
		return client.getAccessToken().getTokenValue();
	}
}
