package org.formation.service.impl;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.formation.config.NotificationServiceConfig;
import org.formation.domain.Livraison;
import org.formation.domain.Livreur;
import org.formation.domain.Status;
import org.formation.interceptor.Logged;
import org.formation.service.Courriel;
import org.formation.service.LivraisonService;
import org.formation.service.NotificationService;

import io.quarkus.logging.Log;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Multi;

@ApplicationScoped
@Logged
public class LivraisonServiceImpl implements LivraisonService {

	@Inject
	NotificationServiceConfig notificationServiceConfig;
	
    @RestClient 
    NotificationService notificationService;
    
    private NotificationService notificationService2;
	
    @Inject
    EntityManager em;
    
	@ConfigProperty(name = "quarkus.http.port") 
	String port;
		
	@PostConstruct
	public void init() {
		notificationService2 = RestClientBuilder.newBuilder()
	            .baseUri(URI.create(notificationServiceConfig.url()))
	            .build(NotificationService.class);
	}
	@SuppressWarnings("unchecked")
	@Override
	@Blocking
	public Multi<Livraison> findAll() {
		Query query = em.createQuery("from Livraison");
		return Multi.createFrom().items(query.getResultStream()); 
	}
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<Livraison> findAllSync() {
		return em.createQuery("from Livraison").getResultList(); 
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<Livraison> findEncours() {
		return Livraison.findEnCours(); 
	}
	
	@Override
	@Transactional
	public Optional<Livraison> load(Livraison livraison) {
		Livraison ret = Livraison.findById(livraison.getId());
		return ret != null ? Optional.of(ret) : Optional.empty();
	}
		
	@Override
	@Transactional
	public Livraison create(String noCommande) {
		Livraison livraison = Livraison.builder().noCommande(noCommande).creationDate(Instant.now()).status(Status.CREE).build();
		Livraison.persist(livraison);
		notificationService.sendMail(Courriel.builder().to("david.thibau@gmail.com").subject("Création Livraison").text(livraison.toString()).build());
		notificationService2.sendMailReactive(Courriel.builder().to("david.thibau@gmail.com").subject("Création Livraison Builder").text(livraison.toString()).build())
			.subscribe().with(e -> Log.info("Reactive Mail Sent " + e));

		return livraison;
	}

	@Override
	@Transactional
	public void affect(Livraison livraison, Livreur livreur) {
		livraison = Livraison.findById(livraison.getId());
		livraison.setLivreur(livreur);
	}

	@Override
	@Transactional
	public void start(Livraison livraison) {
		livraison = Livraison.findById(livraison.getId());
		livraison.setStatus(Status.EN_COURS);
	}

	@Override
	@Transactional
	public void complete(Livraison livraison) {
		Livraison.findById(livraison.getId());
		livraison.setStatus(Status.DISTRIBUE);
	}

}
