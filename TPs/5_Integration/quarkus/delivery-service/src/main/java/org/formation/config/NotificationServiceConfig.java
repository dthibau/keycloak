package org.formation.config;

import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.URL;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "notification")
public interface NotificationServiceConfig {

	String protocol();
	String host();
	String port();
	@URL
	String url();
	
	@NotEmpty
	String token();
}
