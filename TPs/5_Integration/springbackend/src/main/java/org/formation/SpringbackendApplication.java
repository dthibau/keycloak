package org.formation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@SpringBootApplication
@RestController
public class SpringbackendApplication {

	@Autowired
	KeycloakLogoutHandler keycloakLogoutHandler;
	
	@Autowired
	OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
	
	public static void main(String[] args) {
		SpringApplication.run(SpringbackendApplication.class, args);
	}

	@Bean
	public SecurityFilterChain securitygWebFilterChain(HttpSecurity http) throws Exception {
	    return http.authorizeHttpRequests(auth -> auth.requestMatchers("/oidc-principal").hasAuthority("OIDC_USER").anyRequest().authenticated())
	      .oauth2Login(Customizer.withDefaults())
	      .csrf(csrf -> csrf.disable())
	      .logout(logout -> logout.logoutUrl("/logout").addLogoutHandler(keycloakLogoutHandler).invalidateHttpSession(true).logoutSuccessUrl("/"))
	      .build();
	}
	

	@GetMapping("/oidc-principal")
	public OidcUser getOidcUserPrincipal(
	  @AuthenticationPrincipal OidcUser principal) {
		
	    return principal;
	}
	@GetMapping("/access-token")
	public String getAccessToken() {
		OAuth2AuthenticationToken oauthToken  =
				(OAuth2AuthenticationToken)
			    SecurityContextHolder
			        .getContext()
			        .getAuthentication();


			
			OAuth2AuthorizedClient client =
					oAuth2AuthorizedClientService.loadAuthorizedClient(
				            oauthToken.getAuthorizedClientRegistrationId(),
				            oauthToken.getName());

			return client.getAccessToken().getTokenValue();
			
	}
	
	@PostMapping("/sso-logout")
	public void ssoLogout(@RequestParam("logout_token") String logoutToken, HttpSession session) {
		
	    session.invalidate();
	}
}
