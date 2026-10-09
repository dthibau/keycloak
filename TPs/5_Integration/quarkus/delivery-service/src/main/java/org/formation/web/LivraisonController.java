package org.formation.web;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.formation.domain.Livraison;
import org.formation.domain.Livreur;
import org.formation.interceptor.Logged;
import org.formation.service.LivraisonService;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import com.fasterxml.jackson.annotation.JsonView;

import io.quarkus.logging.Log;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Multi;


@Path("/livraisons")
@RolesAllowed("admin")
public class LivraisonController {

	@Inject
	LivraisonService livraisonService;
	
    @GET
    @Logged
    @JsonView(Views.Base.class)
    @Blocking
    @RolesAllowed("user")
	public Multi<Livraison> findAll() {
    	Log.debug("Reactive call ");
		return livraisonService.findAll();
	}

    @GET
    @Path("/sync")
    @Logged
    @JsonView(Views.Base.class)
    @RolesAllowed("user")
	public List<Livraison> findAllSync() {
    	Log.debug("Sync Call ");
		return livraisonService.findAllSync();
	}
    
    @GET
    @Path("/encours")
    @Logged
    @JsonView(Views.Base.class)
    @RolesAllowed("user")
	public List<Livraison> findAllEncours() {
    	Log.debug("Encours Call ");
		return livraisonService.findEncours();
	}

    @GET
    @Path("{id}")
    @JsonView(Views.Complet.class)
    @RolesAllowed("user")
    public Livraison findOne(@RestPath Long id) {
    	return livraisonService.load(Livraison.builder().id(id).build()).orElseThrow(() -> new NotFoundException("No such livraison " + id));
    }

    @ServerExceptionMapper
    public RestResponse<String> mapException(NotFoundException x) {
        return RestResponse.status(Response.Status.NOT_FOUND, x.getMessage());
    }

    @POST
    @ResponseStatus(201)
    public Livraison create(@RestQuery String noCommande) {
    	return livraisonService.create(noCommande);
    }
    

    @PUT
    @Path("{id}/start")
    @ResponseStatus(204)
    public void start(@RestPath Long id) {
    	livraisonService.start(Livraison.builder().id(id).build());
    }
    @PUT
    @Path("{id}/affect/{livreurId}")
    @ResponseStatus(204)
    public void affect(@RestPath Long id, @RestPath Long livreurId) {
    	livraisonService.affect(Livraison.builder().id(id).build(), Livreur.builder().id(livreurId).build());
    }
    @PUT
    @Path("{id}/complete")
    @ResponseStatus(204)
    public void complete(@RestPath Long id, @RestPath Long livreurId) {
    	livraisonService.complete(Livraison.builder().id(id).build());
    }



}
