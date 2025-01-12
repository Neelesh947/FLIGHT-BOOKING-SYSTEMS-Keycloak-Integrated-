package com.keycloak.service;

import java.util.List;

import org.keycloak.representations.idm.UserRepresentation;

import com.keycloak.util.KeyCloakRepresentation;
import com.keycloak.util.LoginResponse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface KeycloakService {

	public LoginResponse getAccessToken(String username, String password);

	public KeyCloakRepresentation createKeycloakUsersAndAssignRoles(KeyCloakRepresentation keyCloakRepresentation, String realm, String token);

	public UserRepresentation updateKeycloakUser(UserRepresentation keyCloakRepresentation,
			@NotNull(message = "User ID must not be null or empty") String userId,
			@NotNull(message = "Realm must not be null or empty") String realm);

	public UserRepresentation deleteKeycloakUser(@NotNull(message = "User Id must not be null") String userId,
			@NotNull(message = "Realm can not be empty") String realm);

	public UserRepresentation getKeycloakUserById(
			@NotBlank(message = "User Id must not be emtpty or blank") String userId,
			@NotBlank(message = "Realm must not be empty or blank") String realm);

	public List<KeyCloakRepresentation> getKeycloakUserByUsername(
			@NotBlank(message = "username can not be blank or empty") String username,
			@NotBlank(message = "Realm can not be blank or empty") String realm);

	public List<KeyCloakRepresentation> getKeycloakUserByEmail(
			@NotBlank(message = "Email can not be blank or empty") String email,
			@NotBlank(message = "Realm can not be blank or empty") String realm);

	public List<UserRepresentation> getKeycloakUserByRoleName(
			@NotBlank(message = "Role Name can not be empty or blank") String roleName);
}