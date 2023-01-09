package org.formation;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.smallrye.mutiny.Uni;

@Path("/notifications")
public class NotificationResource {

	@Inject Mailer mailer;     
	
	@Inject	ReactiveMailer reactiveMailer;   
	
	@POST
	public Email send(Email email) {
		 mailer.send(
	                Mail.withText(email.to,                     
	                    email.subject,
	                    email.text
	                )
	        );
		 email.status = "SENT";
		 return email;
	}
	
	@POST
	@Path("/reactive")                                      
	public Uni<Email> sendReactive(Email email) {       
	    return reactiveMailer.send(                         
	    		  Mail.withText(email.to,                     
		                    email.subject,
		                    email.text
	                )
	        ).replaceWith(() -> {
	        	email.status = "SENT"; 
	        	return email;
	        });
	}
}
