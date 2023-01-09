package org.formation.service;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.smallrye.mutiny.Uni;

@Path("/notifications")
@RegisterRestClient(configKey = "notification-api")
public interface NotificationService {

	@POST
    Courriel sendMail(Courriel couriel);
	
	@POST
	@Path("/reactive")
    Uni<Courriel> sendMailReactive(Courriel couriel);
}
