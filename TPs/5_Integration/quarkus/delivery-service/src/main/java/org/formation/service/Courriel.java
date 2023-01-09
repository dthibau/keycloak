package org.formation.service;

import lombok.Builder;

@Builder
public class Courriel {

	public String to;
	public String subject;
	public String text;
	public String status;
	
}
