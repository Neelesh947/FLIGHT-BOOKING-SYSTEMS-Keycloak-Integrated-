package com.keycloak.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.exception.handler.ServicesInternalExceptionHandler;
import com.exception.model.InternalServerError;
import com.exception.model.InvalidRequest;
import com.exception.model.UnauthorizedRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keycloak.Constants.ErrorConstants;
import com.keycloak.config.KeycloakConfig;
import com.keycloak.util.KeyCloakRepresentation;
import com.keycloak.util.KeycloakErrorResponseDto;
import com.keycloak.util.LoginResponse;
import com.keycloak.util.RoleRepresentation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KeycloakServiceImpl implements KeycloakService{

	@Autowired
	private KeycloakConfig keycloakConfig;
	
	ServicesInternalExceptionHandler servicesInternalExceptionHandler;
	
	public KeycloakServiceImpl(ServicesInternalExceptionHandler servicesInternalExceptionHandler) {
		this.servicesInternalExceptionHandler = servicesInternalExceptionHandler;
	}

	RestTemplate restTemplate = new RestTemplate();
	
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	/*
	 * get access token
	 */
	public LoginResponse getAccessToken(String username, String password) {
        log.info("Starting to get access token for user: {}", username);        

        String url = keycloakConfig.getTokenUrl();
        log.debug("Token URL: {}", url);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Create the request body for the grant type
        String body = "client_id=" + keycloakConfig.getClientID() +
                      "&client_secret=" + keycloakConfig.getClientSecret() +
                      "&grant_type=password" +
                      "&username=" + username +
                      "&password=" + password;
        log.debug("Request body: {}", body);

        HttpEntity<String> entity = new HttpEntity<>(body, httpHeaders);

        // Send the request
        log.debug("Sending POST request to Keycloak token endpoint...");
        ResponseEntity<String> response =null;
        try {	
        	response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch(Exception e) {
        	throw servicesInternalExceptionHandler.handleException(e);
        } 

        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("Successfully obtained access token for user: {}", username);
            String responseBody = response.getBody();
            LoginResponse loginResponse = extractToken(responseBody);
            return loginResponse;
        } else {
            log.error("Failed to obtain access token. Status code: {}. Response: {}", 
                         response.getStatusCode(), response.getBody());
            throw new RuntimeException("Failed to obtain access token: " + response.getStatusCode());
        }
    }

    @SuppressWarnings("unchecked")
	private LoginResponse extractToken(String responseBody) {
        log.debug("Extracting token from response...");
       try {
    	   ObjectMapper objectMapper = new ObjectMapper();
           Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);

           // Extract the token and other fields
           String accessToken = (String) responseMap.get("access_token");
           Integer expiresIn = (Integer) responseMap.get("expires_in");
           String refreshToken = (String) responseMap.get("refresh_token");
           String tokenType = (String) responseMap.get("token_type");
           Integer refreshExpiresIn = (Integer) responseMap.get("refresh_expires_in");
           String scope = (String) responseMap.get("scope");
           String sessionState = (String) responseMap.get("session_state");
           Integer notBeforePolicy = (Integer) responseMap.get("not-before-policy");

           LoginResponse loginResponse = new LoginResponse();
           loginResponse.setAccessToken(accessToken);
           loginResponse.setExpiresIn(expiresIn);
           loginResponse.setRefreshToken(refreshToken);
           loginResponse.setNotBeforePolicy(notBeforePolicy);
           loginResponse.setTokenType(tokenType);
           loginResponse.setRefreshExpiresIn(refreshExpiresIn);
           loginResponse.setScope(scope);
           loginResponse.setSessionState(sessionState);
           return loginResponse;
       } catch (Exception e) {
    	   log.error("Error extracting token from response", e);
           throw new RuntimeException("Failed to parse token response");
       }
    }

    /**
     * Creates a KEYCLOAK user and assigns roles
     * 
     * @param keyCloakRepresentation The user data to be created in KEYCLOAK
     * @return The created user representation
     */
	public KeyCloakRepresentation createKeycloakUsersAndAssignRoles(KeyCloakRepresentation keyCloakRepresentation, String realm, String token) {
		log.info("Initializing creating user...");
		log.info("getting token..");
		if(keyCloakRepresentation.getUsername() == null || keyCloakRepresentation.getPassword() == null
				|| keyCloakRepresentation.getPhoneNumber() == null) {
			throw new InvalidRequest(ErrorConstants.DATA_IS_MISSING);
		}
		List<String> rolesFromToken = getRolesFromToken(token);
		log.info("Roles list: {}", rolesFromToken);
		String accessToken = getAdminAccessToken();
		log.info("access token: {}" ,accessToken);
		String url = "http://localhost:8080/admin/realms/" + realm + "/users";
		
		UserRepresentation userRepresentation = getUserRepresentations(
				getCredentialRepresentations(keyCloakRepresentation), keyCloakRepresentation);
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.setBearerAuth(accessToken);
		
		 HttpEntity<UserRepresentation> entity = new HttpEntity<>(userRepresentation, httpHeaders);
		 ResponseEntity<String> response = null;
		 try {
			 response = restTemplate.postForEntity(url, entity, String.class);
		 } catch(HttpClientErrorException e) {
			 log.error("Something unusual",e);
			 throw new InvalidRequest(e.getMessage());
		 } catch (Exception e) {
			throw new InternalServerError(ErrorConstants.SOME_THING_WENT_WRONG);
		}
		 		 
		 if (response.getStatusCode() == HttpStatus.CREATED) {
	            log.info("User successfully created in Keycloak.");
	            String userId = getUserId(keyCloakRepresentation.getUsername(), realm);	            
	            log.info("Extracted user ID: {}", userId);
	            asignRoleToTheUsers(realm, userId, rolesFromToken);
	            return keyCloakRepresentation;
	     } else {
	            log.error("Failed to create user. Status code: {}. Response: {}", 
	                         response.getStatusCode(), response.getBody());
	            throw new RuntimeException("Failed to create user: " + response.getStatusCode());
	     }
	}
		
	private List<CredentialRepresentation> getCredentialRepresentations(KeyCloakRepresentation keyCloakRepresentation ) {
		List<CredentialRepresentation> credentailsList = new ArrayList<>();
		CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
		credentialRepresentation.setTemporary(false);
		credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
		credentialRepresentation.setValue(keyCloakRepresentation.getPassword());
		credentailsList.add(credentialRepresentation);
		return credentailsList;
	}
	
	private UserRepresentation getUserRepresentations(List<CredentialRepresentation> credentialsList,
			KeyCloakRepresentation keyCloakRepresentation) {
		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setFirstName(keyCloakRepresentation.getFirstName());
		userRepresentation.setLastName(keyCloakRepresentation.getLastName());
		userRepresentation.setEmailVerified(false);
		userRepresentation.setEnabled(true);
		userRepresentation.setUsername(keyCloakRepresentation.getUsername());
		userRepresentation.setEmail(keyCloakRepresentation.getEmail());
		userRepresentation.setCredentials(credentialsList);
		return userRepresentation;
	}
	
	/**
	 * Get access token from client id and DashBoard
	 */
	private String getAdminAccessToken( ) {
		log.info("Getting admin token");
		String url = keycloakConfig.getTokenUrl();
		String body = "client_id=" + keycloakConfig.getClientID()
					+ "&grant_type=client_credentials"
					+ "&client_secret=" + keycloakConfig.getClientSecret();

		HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(body, httpHeaders);
        
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("Successfully obtained DASHBOARD token");
            String responseBody = response.getBody();
            String accessToken = responseBody.split("\"access_token\":\"")[1].split("\"")[0];
            return accessToken;
        } else {
            log.error("Failed to obtain access token. Status code: {}. Response: {}", 
                         response.getStatusCode(), response.getBody());
            throw new RuntimeException("Failed to obtain access token: " + response.getStatusCode());
        }        
	}
	
	/**
	 * get list of roles
	 */
	private List<RoleRepresentation> getRoleList (String realm) {
		try {
	        String accessToken = getAdminAccessToken();
	        String url = "http://localhost:8080/admin/realms/" + realm + "/roles";
	        
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.setBearerAuth(accessToken);

	        HttpEntity<String> entity = new HttpEntity<>(headers);
	        RestTemplate restTemplate = new RestTemplate();
	        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

	        if (response.getStatusCode() == HttpStatus.OK) {
	            String responseBody = response.getBody();
	            JsonNode jsonResponse = new ObjectMapper().readTree(responseBody);
	            
	            List<RoleRepresentation> roleNames = new ArrayList<>();
	            if (jsonResponse.isArray()) {
	                for (JsonNode role : jsonResponse) {
	                	RoleRepresentation roleRepresentation= new RoleRepresentation();
	                	roleRepresentation.setName(role.get("name").asText());
	                	roleRepresentation.setId(role.get("id").asText());
	                	roleNames.add(roleRepresentation);
	                }
	            } else {
	                log.error("No roles found in realm '{}'", realm);
	                throw new RuntimeException("No roles found");
	            }
	            
	            return roleNames;
	        } else {
	            log.error("Failed to fetch roles. Status code: {}", response.getStatusCode());
	            throw new RuntimeException("Failed to fetch roles");
	        }
	    } catch (Exception e) {
	        log.error("Failed to get roles from realm '{}'", realm, e);
	        throw new RuntimeException("Failed to get roles", e);
	    }
	}
	
	/**
	 * get roles from the token
	 */
	private List<String> getRolesFromToken(String accessToken) {
		try {
			String [] tokenParts = accessToken.split("\\.");
			if(tokenParts.length < 2) {
				throw new RuntimeException("Invalid access token");
			}
			 String payload = new String(java.util.Base64.getUrlDecoder().decode(tokenParts[1]));
			 ObjectMapper objectMapper = new ObjectMapper();
	         JsonNode payloadJson = objectMapper.readTree(payload);
	         long exp = payloadJson.path("exp").asLong();
	         long currentTime = System.currentTimeMillis()/1000;
	         if(exp < currentTime) {
	        	 throw new UnauthorizedRequest("Token has expired");
	         }
	         List<String> roles = new ArrayList<>();
	            JsonNode realmAccess = payloadJson.path("realm_access").path("roles");
	            for (JsonNode roleNode : realmAccess) {
	                roles.add(roleNode.asText());
	            }

	            return roles;
		} catch (Exception e) {
            throw new RuntimeException("Error extracting roles from token", e);
        }
	}
	
	/**
	 * Assign role to the desired user
	 */
	private void asignRoleToTheUsers(String realm, String userId, List<String> roles) {
		try {
			String accessToken = getAdminAccessToken();
			String url = "http://localhost:8080/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";
			
			List<RoleRepresentation> roleMappings = new ArrayList<>();	
			for(String roleName : roles) {
				switch (roleName) {
					case "super_admin":
							roleMappings.add(createRoleMapping("admin"));
							break;
					case "admin":
							roleMappings.add(createRoleMapping("customer_support"));
							roleMappings.add(createRoleMapping("flight_operations_manager"));
							roleMappings.add(createRoleMapping("marketing_manager"));
							roleMappings.add(createRoleMapping("it_support"));
							roleMappings.add(createRoleMapping("payment_processor"));
							break;
					case "customer_support":
							roleMappings.add(createRoleMapping("travel_agent"));
							roleMappings.add(createRoleMapping("User"));
							break;
					case "default-roles-dev":
							break;
					default:
	                    log.info("Unknown role: " + roleName);		
				}
			}
			HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.setBearerAuth(accessToken);
	        HttpEntity<List<RoleRepresentation>> entity = new HttpEntity<>(roleMappings, headers);
			
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
	            log.info("Roles successfully assigned to user '{}'", userId);
	        } else {
	            log.error("Failed to assign roles to user '{}'. Status code: {}. Response: {}", userId, response.getStatusCode(), response.getBody());
	            throw new RuntimeException("Failed to assign roles to user");
	        }
			
		} catch (Exception e) {
	        log.error("Failed to get roles from realm '{}'", realm, e);
	        throw new RuntimeException("Failed to get roles", e);
	    }
	}
	
	/**
	 * create roleName
	 * @param roleName
	 * @return
	 */
	private RoleRepresentation createRoleMapping(String roleName) {
		RoleRepresentation roleRepresentation = new RoleRepresentation();
	    roleRepresentation.setName(roleName);
	    String realm = "dev";
	    List<RoleRepresentation> rolesList = getRoleList(realm);
	    for(RoleRepresentation role: rolesList) {
	    	if(role.getName().equals(roleName)) {
	    		roleRepresentation.setId(role.getId());
	    	}
	    }
	    return roleRepresentation;
	}
	
	/**
	 * get userID
	 */
	private String getUserId(String username, String realm) {
	    try {
	        String accessToken = getAdminAccessToken();
	        String url = "http://localhost:8080/admin/realms/" + realm + "/users?username=" + username;
	        
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.setBearerAuth(accessToken);

	        HttpEntity<String> entity = new HttpEntity<>(headers);
	        RestTemplate restTemplate = new RestTemplate();
	        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

	        if (response.getStatusCode() == HttpStatus.OK) {
	            String responseBody = response.getBody();
	            JsonNode jsonResponse = new ObjectMapper().readTree(responseBody);

	            if (jsonResponse.isArray() && jsonResponse.size() > 0) {
	                return jsonResponse.get(0).get("id").asText();
	            } else {
	                log.error("User with username '{}' not found in realm '{}'", username, realm);
	                throw new RuntimeException("User not found");
	            }
	        } else {
	            log.error("Failed to fetch user details. Status code: {}", response.getStatusCode());
	            throw new RuntimeException("Failed to fetch user details");
	        }
	    } catch (Exception e) {
	        log.error("Failed to extract user ID for username '{}' in realm '{}'", username, realm, e);
	        throw new RuntimeException("Failed to extract user ID from response", e);
	    }
	}

	/**
	 * update keyCloak user
	 */
	@Override
	public UserRepresentation updateKeycloakUser(UserRepresentation keyCloakRepresentation,
			@NotNull(message = "User ID must not be null or empty") String userId,
			@NotNull(message = "Realm must not be null or empty") String realm) {
		log.info("Updating user started for userId: {}", userId);
		try {
			String accessToken = getAdminAccessToken();
			String url = "http://localhost:8080/admin/realms/" + realm +"/users/" + userId;
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(accessToken);
			
			String requestBody = convertToJson(keyCloakRepresentation);
			
			 HttpEntity<String> entity = new HttpEntity<>(requestBody,headers);
		     		     
		     ResponseEntity<UserRepresentation> response = restTemplate.exchange(
		    		 url, HttpMethod.PUT, entity, UserRepresentation.class);
		     HttpStatusCode statusCode = response.getStatusCode(); 
		     if(statusCode.is4xxClientError() || statusCode.is5xxServerError()) {
		    	 String responseString = convertToJson(response.getBody());
		    	 KeycloakErrorResponseDto errorResponse = OBJECT_MAPPER.readValue(responseString, 
		    			 	KeycloakErrorResponseDto.class);
		    	 throw new InvalidRequest("Error updating user: " + errorResponse.getErrorMessage());
		     }
		     log.info("User updated successfully for userId: {}", userId);
		     return response.getBody();
		} catch (InvalidRequest e) {
			throw e;
		} catch (HttpClientErrorException e) {
			throw e;
		} catch (Exception e) {
			log.error("unable to update the user with userId: {}", userId);
			throw new InternalServerError("not able to update the user");
		}
	}
	
	private String convertToJson(UserRepresentation keyCloakRepresentation) {
		try {
			return OBJECT_MAPPER.writeValueAsString(keyCloakRepresentation);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to convert KeyCloakRepresentation to JSON", e);
		}
	}

	/**
	 * delete keyCloak users
	 */
	@Override
	public UserRepresentation deleteKeycloakUser(@NotNull(message = "User Id must not be null") String userId,
			@NotNull(message = "Realm can not be empty") String realm) {
		log.info("user deletion started for userId: {}", userId);
		try {
			String accessToken = getAdminAccessToken();
			String url = "http://localhost:8080/admin/realms/" + realm +"/users/" + userId;
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(accessToken);
			
			HttpEntity<String> entity = new HttpEntity<>(headers);
			ResponseEntity<UserRepresentation> response = restTemplate.exchange(url, 
						HttpMethod.DELETE, entity, UserRepresentation.class);
			HttpStatusCode statusCode = response.getStatusCode(); 
		     if(statusCode.is4xxClientError() || statusCode.is5xxServerError()) {
		    	 String responseString = convertToJson(response.getBody());
		    	 KeycloakErrorResponseDto errorResponse = OBJECT_MAPPER.readValue(responseString, 
		    			 	KeycloakErrorResponseDto.class);
		    	 throw new InvalidRequest("Error updating user: " + errorResponse.getErrorMessage());
		     }
		     log.info("User deleted successfully for userId: {}", userId);
		     return response.getBody();
			
		} catch(InvalidRequest e) {
			log.error("Invalid request", e.getMessage());
			throw e;
		} catch(HttpClientErrorException e) {
			log.error("User not found", e.getMessage());
			throw e;
		} catch(Exception e) {
			throw new RuntimeException("Failed delete the user with error", e);
		}
	}

	/**
	 * get keyCloak user based on userId
	 */
	@Override
	public UserRepresentation getKeycloakUserById(
			@NotBlank(message = "User Id must not be emtpty or blank") String userId,
			@NotBlank(message = "Realm must not be empty or blank") String realm) {
		log.info("Finding user with userId: {}", userId);
		try {
			String accessToken = getAdminAccessToken();
			String url = "http://localhost:8080/admin/realms/" + realm + "/users/" + userId;
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(accessToken);
			
			HttpEntity<String> entity = new HttpEntity<>(headers);
			ResponseEntity<UserRepresentation> response = restTemplate.exchange(
					url, HttpMethod.GET, entity, UserRepresentation.class);
			HttpStatusCode statusCode = response.getStatusCode(); 
		     if(statusCode.is4xxClientError() || statusCode.is5xxServerError()) {
		    	 String responseString = convertToJson(response.getBody());
		    	 KeycloakErrorResponseDto errorResponse = OBJECT_MAPPER.readValue(responseString, 
		    			 	KeycloakErrorResponseDto.class);
		    	 throw new InvalidRequest("Error updating user: " + errorResponse.getErrorMessage());
		     }
		     log.info("User fetched successfully for userId: {}", userId);
		     return response.getBody();
			
		} catch(InvalidRequest e) {
			throw e;
		} catch (Exception e) {
			log.error("Http Error logs", e);
			throw new InternalServerError("Some exception occured");
		}
	}

	/**
	 * get list of keyCloak user by userName
	 */
	@Override
	public List<KeyCloakRepresentation> getKeycloakUserByUsername(
			@NotBlank(message = "username can not be blank or empty") String username,
			@NotBlank(message = "Realm can not be blank or empty") String realm) {
		log.info("Getting user by username: {} initiated", username);
		try {
			String accessToken = getAdminAccessToken();
			String url = "http://localhost:8080/admin/realms/" + realm + "/users?username=" + username;
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(accessToken);
			
			HttpEntity<String> entity = new HttpEntity<>(headers);
			ResponseEntity<List<KeyCloakRepresentation>> response = restTemplate.exchange(
					url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<KeyCloakRepresentation>>() {});
			HttpStatusCode statusCode = response.getStatusCode(); 
		     if(statusCode.is4xxClientError() || statusCode.is5xxServerError()) {
		    	 String responseString = response.getBody().toString();
		    	 KeycloakErrorResponseDto errorResponse = OBJECT_MAPPER.readValue(responseString, 
		    			 	KeycloakErrorResponseDto.class);
		    	 throw new InvalidRequest("Error updating user: " + errorResponse.getErrorMessage());
		     }
		     log.info("User fetched successfully for userId: {}", username);
		     return response.getBody();
		} catch(InvalidRequest e) {
			throw e;
		} catch (HttpClientErrorException | HttpServerErrorException e) {
		    log.error("HTTP error: {}", e.getMessage(), e);
		    throw new InvalidRequest("Error with the request to Keycloak: " + e.getStatusText());
		} catch (Exception e) {
		    log.error("Unexpected error", e);
		    throw new InternalServerError("Unexpected error occurred");
		}
	}

	/**
	 * get list of keyCloak user by email
	 */
	@Override
	public List<KeyCloakRepresentation> getKeycloakUserByEmail(
			@NotBlank(message = "Email can not be blank or empty") String email,
			@NotBlank(message = "Realm can not be blank or empty") String realm) {
		log.info("Getting user by email: {} initiated", email);
		try {
			String accessToken = getAdminAccessToken();
			String url = "http://localhost:8080/admin/realms/" + realm + "/users?exact=true&email=" + email;
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(accessToken);
			
			HttpEntity<String> entity = new HttpEntity<>(headers);
			ResponseEntity<List<KeyCloakRepresentation>> response = restTemplate.exchange(
					url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<KeyCloakRepresentation>>() {});
			HttpStatusCode statusCode = response.getStatusCode(); 
		     if(statusCode.is4xxClientError() || statusCode.is5xxServerError()) {
		    	 String responseString = response.getBody().toString();
		    	 KeycloakErrorResponseDto errorResponse = OBJECT_MAPPER.readValue(responseString, 
		    			 	KeycloakErrorResponseDto.class);
		    	 throw new InvalidRequest("Error updating user: " + errorResponse.getErrorMessage());
		     }
		     log.info("User fetched successfully for email: {}", email);
		     return response.getBody();
		} catch(InvalidRequest e) {
			throw e;
		} catch (HttpClientErrorException | HttpServerErrorException e) {
		    log.error("HTTP error: {}", e.getMessage(), e);
		    throw new InvalidRequest("Error with the request to Keycloak: " + e.getStatusText());
		} catch (Exception e) {
		    log.error("Unexpected error", e);
		    throw new InternalServerError("Unexpected error occurred");
		}
	}

	@Override
	public List<UserRepresentation> getKeycloakUserByRoleName(
			@NotBlank(message = "Role Name can not be empty or blank") String roleName) {
		log.info("Finding user role...");
		String accessToken = getAdminAccessToken();
		String url = "http://localhost:8080/admin/realms/dev/roles/" + roleName + "/users";
		
		HttpHeaders headers= new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(accessToken);
		
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<List<UserRepresentation>> response = null;
		try {
			response = restTemplate.exchange(url, 
					HttpMethod.GET, entity, new ParameterizedTypeReference<List<UserRepresentation>>() {});
		} catch(HttpClientErrorException e) {
			 log.error("Something unusual",e);
			 throw new InvalidRequest(e.getMessage());
		} catch (Exception e) {
			throw new InternalServerError(ErrorConstants.SOME_THING_WENT_WRONG);
		}
		
		if(response.getStatusCode() == HttpStatus.OK) {
			return response.getBody();
		} else {
            log.error("Failed to get user. Status code: {}. Response: {}", 
                    response.getStatusCode(), response.getBody());
            throw new RuntimeException("Failed to get user: " + response.getStatusCode());
		}
	}
}
