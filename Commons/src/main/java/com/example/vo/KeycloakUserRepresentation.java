package com.example.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class KeycloakUserRepresentation {

	private String username;
	private String email;
	@NotBlank(message = "firstName should not be blank")
	private String firstName;
	@NotBlank(message = "lastName should not be blank")
	private String lastName;
	private String userType;
	private String realm;
	private String password;
	private String phoneNumber;
	private String companyName;
	private boolean enabled;
	private String address;
	private String city;
	private String postalCode;
	private String state;
	private String country;
	private String customerSupportNumber;
	private String adminId;
	private String countryCode;
	private String clientName;
	
	
}
