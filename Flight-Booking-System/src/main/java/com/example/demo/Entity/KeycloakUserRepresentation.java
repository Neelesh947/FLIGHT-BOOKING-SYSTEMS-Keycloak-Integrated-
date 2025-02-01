package com.example.demo.Entity;

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
	private String emailAddress;
	@NotBlank(message = "first name should not be blank")
	private String firstName;
	@NotBlank(message = "last name should not be blank")
	private String lastName;
	private String password;
	private String phoneNumber;
	private boolean enabled;
	private String companyName;
	private String address;
	private String city;
	private String postalCode;
	private String state;
	private String country;
	private String customerSupportNumber;
	private String countryCode;
	private String adminId;	
}
