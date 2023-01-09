package org.formation.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.formation.web.Views;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Review {

	@JsonView(Views.Complet.class)
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	public long id;
	
	@JsonView(Views.Complet.class)
	public int note;
	
	@JsonView(Views.Complet.class)
	public String commentaire;
}
