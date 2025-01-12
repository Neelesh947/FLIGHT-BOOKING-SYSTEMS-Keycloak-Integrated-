package com.keycloak.util;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeyCloakRepresentation {

    private String username;
	
	private String password;
	
	private String email;
		
	private String userType;
			
	private String realm;
	
	private String firstName;
	
	private String lastName;
	
	private boolean isEnabled;
	
	private String phoneNumber;
	
	private String address;
	
	private String city;
	
	private String postalCode;
	
	private String state;
	
	private String country;
	
	private String customerSupportNumber;
	
	private String companyName;
	
	
	private List<Credential> credentials;
	
	@Getter
	@Setter
	public static class Credential {
	        private String type;
	        private String value;
	        private boolean temporary;
	}
}
