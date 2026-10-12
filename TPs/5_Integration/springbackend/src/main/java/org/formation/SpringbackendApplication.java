package org.formation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
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
		// Déconnexion RP-initiated (OpenID Connect RP-Initiated Logout) : après la déconnexion locale,
		// le navigateur est redirigé vers le end session endpoint de Keycloak avec id_token_hint,
		// puis Keycloak renvoie vers post_logout_redirect_uri.
		var oidcLogoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
		oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/");

		return http
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/").permitAll()
				.requestMatchers("/oidc-principal").hasAuthority("OIDC_USER")
				.anyRequest().authenticated())
			// Login OpenID Connect avec le client "springapp" déclaré dans application.yml
			.oauth2Login(Customizer.withDefaults())
			// Back-channel logout : Keycloak notifie l'application sur
			// POST /logout/connect/back-channel/springapp et la session HTTP correspondante est invalidée
			.oidcLogout(oidc -> oidc.backChannel(Customizer.withDefaults()))
			.logout(logout -> logout.logoutSuccessHandler(oidcLogoutSuccessHandler))
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
