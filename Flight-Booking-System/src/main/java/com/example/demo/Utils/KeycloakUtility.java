package com.example.demo.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.demo.config.Constants;
import com.exception.model.InternalServerError;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KeycloakUtility {
	
	RestTemplate restTemplate = new RestTemplate();
		
	private static final String KEYCLOAK_URL = "http://localhost:8081";
	
	public List<UserRepresentation> allUsersOfSpecificRoleAndRealm(String role, Map<String, Object> allParams,
			String realm) {
		String url = KEYCLOAK_URL + "/" + realm + "/user/by/role/"  + role;
		HttpEntity<Map<String, Object>> requestMap = new HttpEntity<Map<String,Object>>(
							allParams != null ? allParams : new HashMap<>());
		try {
			ResponseEntity<List<UserRepresentation>> response = restTemplate.exchange(url, HttpMethod.GET, requestMap,
						new ParameterizedTypeReference<List<UserRepresentation>>() {
						});
			return response.getBody();
		} catch (Exception e) {
			log.error("Exception occurred", e);
			throw new InternalServerError(e.getMessage());
		}
	}
	
	public List<UserRepresentation> userByEmailAndRole(String email, String role, String realm) {
		String url = KEYCLOAK_URL + "/" + realm + "/user/by/EmailAndRole/"  + email + "/" + role;
		try {
			ResponseEntity<List<UserRepresentation>> response = restTemplate.exchange(url, HttpMethod.GET, null,
						new ParameterizedTypeReference<List<UserRepresentation>>() {
						});
			return response.getBody();
		} catch (Exception e) {
			log.error("Exception occurred", e);
			throw new InternalServerError(e.getMessage());
		}
	}
	
	public List<UserRepresentation> getKeycloakUserByUsername(String username, String realm) {
		String url = KEYCLOAK_URL + "/" + realm + "/user/by/"  + username;
		try {
			ResponseEntity<List<UserRepresentation>> response = restTemplate.exchange(url, HttpMethod.GET, null,
						new ParameterizedTypeReference<List<UserRepresentation>>() {
						});
			return response.getBody();
		} catch (Exception e) {
			log.error("Exception occurred", e);
			throw new InternalServerError(e.getMessage());
		}
	}
	
	public <T>Map<String, String> createUser(T userObject, String role, String realm) {
		Map<String, String> requestmap = new HashMap<>();
		String url = KEYCLOAK_URL + "/" + realm + "/create-user/"  + role;
		HttpEntity<?> reqmap = new HttpEntity<T>(userObject);
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, reqmap, String.class);
			if(response.getStatusCode() == HttpStatus.CREATED) {
				String userId = response.getBody();
				requestmap.put(Constants.STATUS, Constants.SUCCESS);
				requestmap.put(Constants.MESSAGE, ErrorConstants.USER_CREATED);
				requestmap.put("userId", userId);
				return requestmap;
			}
		} catch(Exception e) {
			log.error("Some exception occurred", e);
			throw new InternalServerError(ErrorConstants.INTERNAL_SERVER_ERROR);
		}
		return null;
	}
	
	public UserRepresentation userById(String userId, String realm) {
		String url = KEYCLOAK_URL + "/" + realm + "/user/" + userId;
		try {
			ResponseEntity<UserRepresentation> response = restTemplate.exchange(url, HttpMethod.GET,
					null, UserRepresentation.class);
			return response.getBody();
		} catch(Exception e) {
			log.error("Exception occurred", e);
			throw new InternalServerError(e.getMessage());
		}
	}

	public void updateUser(UserRepresentation userResponse, String userId, String realm) {
		String url = KEYCLOAK_URL + "/" + realm + "/update/user/" + userId;
		HttpEntity<UserRepresentation> entity = new HttpEntity<>(userResponse);
		try {
			restTemplate.exchange(url, HttpMethod.PUT, entity, UserRepresentation.class);
		} catch (Exception e) {
			log.error("Exception occurred", e);
			throw new InternalServerError(e.getMessage());
		}
	}
}