package com.example.demo.serviceImpl;

import java.util.List;
import java.util.Map;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.Admin;
import com.example.demo.Utils.ErrorConstants;
import com.example.demo.service.AdminService;
import com.exception.model.DataUnavailable;
import com.exception.model.InternalServerError;
import com.exception.model.InvalidRequest;
import com.keycloak.service.KeycloakService;
import com.keycloak.util.KeyCloakRepresentation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService{
	
	private String roleNameOfAdmin = "admin";
	
	@Autowired
	private KeycloakService keycloakService;

	@Override
	public ResponseEntity<?> createAdmin(Admin admin, String roleNameOfAdmin, String realm) {
		log.info("AdminServiceImpl: create Admin invoked for admin: {}", admin.getUsername());
		KeyCloakRepresentation response = null;
		validateAdmin(admin, realm);
		try {
			response = keycloakService.createKeycloakUsersAndAssignRoles(admin, realm, realm, roleNameOfAdmin);
		} catch (InternalServerError e) {
			throw new DataUnavailable(ErrorConstants.ADMIN_NOT_CREATED);
		}
		
		return null;
	}

	public boolean validateAdmin(Admin admin, String realm) {
		List<UserRepresentation> existingUserWithEmailAndRolesInKeycloak = keycloakService
				.getKeyCloakUserByEmailAndRoles(admin.getEmail(), roleNameOfAdmin, realm);	
		if(!existingUserWithEmailAndRolesInKeycloak.isEmpty()) {
			log.error("User already exist with same username: {}",  admin.getUsername());
			throw new InvalidRequest(ErrorConstants.EMAIL_ALREADY_EXISTS);
		}
		List<UserRepresentation> existingUserWithUsernameInKeycloak = keycloakService
				.getKeycloakUserByUsername(admin.getUsername(), realm);
		if(!existingUserWithUsernameInKeycloak.isEmpty()) {
			log.error("User already exist with same username: {}",  admin.getUsername());
			throw new InvalidRequest(ErrorConstants.USERNAME_ALREADY_EXIST);
		}
		return true;
	}
	
}
