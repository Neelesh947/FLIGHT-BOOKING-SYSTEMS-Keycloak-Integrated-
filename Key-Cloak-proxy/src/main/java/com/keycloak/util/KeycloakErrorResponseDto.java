package com.keycloak.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KeycloakErrorResponseDto {

	private String error;
	
	@JsonProperty("error_description")
	private String errorDescription;
	
	private String errorMessage;
}
