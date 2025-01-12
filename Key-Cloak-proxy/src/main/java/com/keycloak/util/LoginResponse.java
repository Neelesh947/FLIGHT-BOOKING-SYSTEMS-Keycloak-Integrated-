package com.keycloak.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

	private String accessToken;
	
	private Integer expiresIn;
	
	private Integer refreshExpiresIn;
	
	private String refreshToken;
	
	private String tokenType;
	
	private Integer notBeforePolicy;
	
	private String sessionState;
	
	private String scope;
}
