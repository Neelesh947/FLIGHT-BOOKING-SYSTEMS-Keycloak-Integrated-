package com.example.demo.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Admin{

	private String id;
	
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
}
