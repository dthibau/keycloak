package org.formation.service;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.quarkus.oidc.token.propagation.common.AccessToken;
import io.smallrye.mutiny.Uni;

// @AccessToken : le jeton d'accès de la requête entrante est propagé dans l'en-tête Authorization
// des appels sortants (extension quarkus-rest-client-oidc-token-propagation)
@Path("/notifications")
@RegisterRestClient(configKey = "notification-api")
@AccessToken
public interface NotificationService {

	@POST
    Courriel sendMail(Courriel couriel);
	
	@POST
	@Path("/reactive")
    Uni<Courriel> sendMailReactive(Courriel couriel);
}
