package org.formation.domain;

import java.time.Instant;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.formation.web.Views;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Livreur {

	@JsonView(Views.Base.class)
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	public long id;
	
	@JsonView(Views.Complet.class)
	public String nom;
	
	@JsonView(Views.Complet.class)
	public String telephone;

	@JsonView(Views.Complet.class)
	@OneToMany(cascade = CascadeType.ALL)
	public List<Review> reviews;
	
}
