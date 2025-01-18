package com.keycloak.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Component
public class KeycloakConfig {

	@Value("${keycloak.token.url}")
	private String tokenUrl;
	
	@Value("${keycloak.admin.clientid}")
	private String adminClient;
	
	@Value("${keycloak.admin.username}")
	private String adminUsername;
	
	@Value("${keycloak.admin.password}")
	private String adminPassword;
		
	@Value("${keycloak.client.id}")
	private String clientID;
	
	@Value("${keycloak.client.secret}")
	private String clientSecret;
	
	@Value("${keycloak.user.session.clear.URL}")
	private String userSessionClearURL;
	
}
