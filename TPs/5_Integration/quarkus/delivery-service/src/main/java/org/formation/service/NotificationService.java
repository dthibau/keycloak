package org.formation.service;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.smallrye.mutiny.Uni;

// 5.3.1 : ajouter l'annotation @AccessToken (io.quarkus.oidc.token.propagation.common.AccessToken)
// pour propager le jeton d'accès reçu vers notification-service
@Path("/notifications")
@RegisterRestClient(configKey = "notification-api")
public interface NotificationService {

	@POST
    Courriel sendMail(Courriel couriel);
	
	@POST
	@Path("/reactive")
    Uni<Courriel> sendMailReactive(Courriel couriel);
}
