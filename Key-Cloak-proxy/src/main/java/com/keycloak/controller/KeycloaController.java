package com.keycloak.controller;

import java.util.List;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.keycloak.service.KeycloakService;
import com.keycloak.util.KeyCloakRepresentation;
import com.keycloak.util.LoginRequest;
import com.keycloak.util.LoginResponse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/{realm}/")
public class KeycloaController {

	private final KeycloakService keycloakService;
	
	public KeycloaController(KeycloakService keycloakService) {
		this.keycloakService = keycloakService;
	}
	
	/**
	 * Login User
	 */
	@PostMapping("login")
	public ResponseEntity<LoginResponse> loginUser (@RequestBody LoginRequest request, @PathVariable String realm) {
		log.info("login initiated...");
		try {
            LoginResponse token = keycloakService.getAccessToken(request.getUsername(), request.getPassword());
            log.info("user logged in successfully: {}", request.getUsername());
            return ResponseEntity.ok(token);
        } catch (Exception e) {
        	log.error("Loging error for user: {} in realm: {}", request.getUsername(), realm);
            throw e;
        }
	}
	
	/**
	 * API for create users
	 * @param keyCloakRepresentation
	 * @return
	 */
	@PostMapping("create-user")
	public ResponseEntity<KeyCloakRepresentation> createKeycloakUser (@RequestBody KeyCloakRepresentation keyCloakRepresentation, 
			@PathVariable String realm, @RequestHeader("Authorization") String token) {
		log.info("Initiated user creation...");
		String roleName = "admin";
		KeyCloakRepresentation users = keycloakService.createKeycloakUsersAndAssignRoles(keyCloakRepresentation, realm, token, roleName);
		return ResponseEntity.status(HttpStatus.CREATED).body(users);
	}
	
	/**
	 * Update KeyCloak user
	 * @param keyCloakRepresentation
	 * @param userId
	 * @param realm
	 * @return
	 */
	@PutMapping("update/user/{userId}")
	public ResponseEntity<UserRepresentation> updateKeycloakUser (@RequestBody UserRepresentation keyCloakRepresentation,
			@NotNull(message = "User ID must not be null or empty") @PathVariable String userId,
			@NotNull(message = "Realm must not be null or empty") @PathVariable String realm) {
		log.debug("Updating user for userId: {} in realm: {}", userId, realm);
		try {
			UserRepresentation updateUser = keycloakService.updateKeycloakUser(keyCloakRepresentation, userId, realm);
			return ResponseEntity.ok(updateUser);
		} catch(Exception e) {
			log.error("user update failed for userId: {}", userId);
			throw e;
		}
	} 
	
	/**
	 * Delete keyCloak user
	 * @param userId
	 * @param realm
	 * @return
	 */
	@DeleteMapping("/delete/user/{userId}")
	public ResponseEntity<UserRepresentation> deleteKeyCloakUser(@NotNull(message = "User Id must not be null") 
	@PathVariable String userId, @NotNull(message = "Realm can not be empty") @PathVariable String realm) {
		log.info("User delete invoked for userId: {}", userId);
		try {
			UserRepresentation delete = keycloakService.deleteKeycloakUser(userId, realm);
			log.info("user with userId: {} has been successfully deleted", userId);
			return ResponseEntity.ok(delete);			
		} catch (Exception e) {
			log.error("User with userId: {} deletion failed",userId);
			throw e;
		}
	}
	
	/**
	 * get keyCloak user by userId
	 * @param userId
	 * @param realm
	 * @return
	 */
	@GetMapping("user/{userId}")
	public ResponseEntity<UserRepresentation> getKeycloakUserById(
			@NotBlank(message = "User Id must not be emtpty or blank") @PathVariable String userId,
			@NotBlank(message = "Realm must not be empty or blank") @PathVariable String realm) {
		log.info("Getting user with userId: {}", userId);
		try {
			UserRepresentation getUser = keycloakService.getKeycloakUserById(userId, realm);
			return ResponseEntity.status(HttpStatus.OK).body(getUser);
		} catch(Exception e) {
			log.info("Error in getting user with userId", userId);
			throw e;
		}
	}
	
	/**
	 * get keyCloak user by user name
	 * @param user name
	 * @param realm
	 * @return
	 */
	@GetMapping("user/by/{username}")
	public ResponseEntity<List<UserRepresentation>> getKeycloakUserByUsername(
			@NotBlank(message = "username can not be blank or empty") @PathVariable String username, 
			@NotBlank(message = "Realm can not be blank or empty") @PathVariable String realm) {
		log.info("getting user by username: {}", username);
		try {
			List<UserRepresentation> user = keycloakService.getKeycloakUserByUsername(username, realm);
			return ResponseEntity.ok(user);
		} catch(Exception e) {
			log.error("Error in getting username: {}", username);
			throw e;
		}
	}
	
	/**
	 * get user by email
	 * @param email
	 * @param realm
	 * @return
	 */
	@GetMapping("user/by/email/{email}")
	public ResponseEntity<List<UserRepresentation>> getKeycloakUserByEmail(
			@NotBlank(message = "Email can not be blank or empty") @PathVariable String email, 
			@NotBlank(message = "Realm can not be blank or empty") @PathVariable String realm) {
		log.info("getting user by email: {}", email);
		try {
			List<UserRepresentation> user = keycloakService.getKeycloakUserByEmail(email, realm);
			return ResponseEntity.ok(user);
		} catch(Exception e) {
			log.error("Error in getting username: {}", email);
			throw e;
		}
	}
	
	/**
	 * 
	 * @param roleName
	 * @return
	 */
	@GetMapping("user/by/role/{roleName}")
	public ResponseEntity<List<UserRepresentation>> getUserByRoles(
			@NotBlank(message = "Role Name can not be empty or blank") @PathVariable String roleName,
			@NotBlank(message = "Realm must not be empty or blank") @PathVariable String realm){
		log.info("User started finding by roleName: {}",roleName);
		try {
			List<UserRepresentation> userList = keycloakService.getKeycloakUserByRoleName(roleName, realm);
			return ResponseEntity.ok(userList);
		}catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * Get users by email and roles
	 * @param email
	 * @param roleName
	 * @param realm
	 * @return
	 */
	@GetMapping("user/by/EmailAndRole/{email}/{roleName}")
	public ResponseEntity<List<UserRepresentation>> getUserByEmailAndRoles(
			@NotBlank(message = "Email must not be blank or null") @PathVariable String email,
			@NotBlank(message = "Role Name must not be blank or empty") @PathVariable String roleName,
			@NotBlank(message = "Realm must not be empty or blank") @PathVariable String realm){
		log.info("getting user by email: {}, role name: {} in ream: {}",email, roleName, realm);
		try {
			List<UserRepresentation> usersList = keycloakService.getKeyCloakUserByEmailAndRoles(email, roleName, realm);
			return ResponseEntity.ok(usersList);
		}catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * Getting user by phone number and roles
	 * @param phoneNumber
	 * @param roleName
	 * @param realm
	 * @return
	 */
	@GetMapping("user/by/PhoneAndRoles/{phoneNumber}/{roleName}")
	public ResponseEntity<List<UserRepresentation>> keycloakUserByPhoneAndRole(
			@NotBlank(message = "Phone must not be null or Empty") @PathVariable String phoneNumber,
			@NotBlank(message = "Role must not be null or Empty") @PathVariable String roleName,
			@NotBlank(message = "Realm must not be null or Empty") @PathVariable String realm){
		log.info("Started getting user by phonenumber: {} and role: {} in realm: {}", phoneNumber, roleName, realm);
		try {
			List<UserRepresentation> userList = keycloakService.getListOfKeycloakUserByphonenumberAndRole(
							phoneNumber, roleName, realm);
			return ResponseEntity.ok(userList);
		}catch (Exception e) {
			throw e;
		}
	}
	
	@GetMapping("user/by/UsernameAndRole/{username}/{roleName}")
	public ResponseEntity<List<UserRepresentation>> getUserByUsernameAndRoles(
			@NotBlank(message = "Email must not be blank or null") @PathVariable String username,
			@NotBlank(message = "Role Name must not be blank or empty") @PathVariable String roleName,
			@NotBlank(message = "Realm must not be empty or blank") @PathVariable String realm){
		log.info("getting user by email: {}, role name: {} in ream: {}",username, roleName, realm);
		try {
			List<UserRepresentation> usersList = keycloakService.getKeyCloakUserByUsernameAndRoles(username, roleName, realm);
			return ResponseEntity.ok(usersList);
		}catch (Exception e) {
			throw e;
		}
	}
}
