package com.example.demo.serviceImpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.AdminAndFlightManagerMapping;
import com.example.demo.Utils.ErrorConstants;
import com.example.demo.Utils.KeycloakUtility;
import com.example.demo.repository.AdminAndFlightManagerMappingRepository;
import com.example.demo.service.FlightOperationManagerService;
import com.example.vo.FlightOperationManager;
import com.exception.model.DataUnavailable;
import com.exception.model.InternalServerError;
import com.exception.model.InvalidRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FlightOperationManagerServiceImpl implements FlightOperationManagerService{
	
	@Autowired
	private KeycloakUtility keycloakUtility;
	
	@Value("${keycloak.security-constraints[0].authRoles[4]}")
	private String roleNameOfFOM;
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	private AdminAndFlightManagerMappingRepository adminAndFlightManagerMappingRepository;

	/**
	 * Creating the Flight Operation Manager
	 */
	public ResponseEntity<Object> createFlightOperationmanager(String adminId,
			FlightOperationManager flightOperationManager, String realm) {
		log.info("invoked creating for flightOperationManager", flightOperationManager);
		validateFlightOperationmanager(flightOperationManager, adminId, realm);
		try {
			Map<String, String> response = keycloakUtility.createUser(flightOperationManager, roleNameOfFOM, realm);
			String flightOperationmanagerString = response.get("userId").toString();
			JsonNode rootNode = objectMapper.readTree(flightOperationmanagerString);
			String flightOperationmanagerId = rootNode.path("userId").asText();
			if(flightOperationManager == null) {
				throw new DataUnavailable(ErrorConstants.NOT_FOUND);
			}
			AdminAndFlightManagerMapping flightManagerMapping = createAdminAndFlightManagerMapping(
					adminId, flightOperationmanagerId);
			adminAndFlightManagerMappingRepository.save(flightManagerMapping);
			return ResponseEntity.status(HttpStatus.CREATED).body(flightOperationmanagerString);
		} catch (Exception e) {
			log.error("some exception occurred", e.getMessage());
			throw new InternalServerError(ErrorConstants.INTERNAL_SERVER_ERROR);
		}
	}
	
	private AdminAndFlightManagerMapping createAdminAndFlightManagerMapping(String adminId,
			String flightOperationmanagerId) {
		AdminAndFlightManagerMapping flightManagerMapping = new AdminAndFlightManagerMapping();
		flightManagerMapping.setAdminId(adminId);
		flightManagerMapping.setFlightmanagerId(flightOperationmanagerId);
		flightManagerMapping.setCreateDateTime(Timestamp.valueOf(LocalDateTime.now()));
		flightManagerMapping.setUpdateDateTime(Timestamp.valueOf(LocalDateTime.now()));
		flightManagerMapping.setId(UUID.randomUUID().toString());
		return flightManagerMapping;
	}
	
	private boolean validateFlightOperationmanager(FlightOperationManager flightOperationManager,
			String adminId, String realm) {
		if(flightOperationManager.getUsername() == null || flightOperationManager.getUsername().isEmpty()) {
			throw new InvalidRequest(ErrorConstants.USERNAME_CANNOT_BE_EMPTY);
		}
		if(flightOperationManager.getPassword() == null || flightOperationManager.getPassword().isEmpty()) {
			throw new InvalidRequest(ErrorConstants.PASSWORD_CANNOT_BE_EMPTY);
		}
		if(flightOperationManager.getEmail() == null || flightOperationManager.getEmail().isEmpty()) {
			throw new InvalidRequest(ErrorConstants.EMAIL_CANNOT_BE_EMPTY);
		}
		
		List<UserRepresentation> existingUserWithPhoneNumber = keycloakUtility.usersByPhoneAndRole(
				flightOperationManager.getPhoneNumber(), roleNameOfFOM, realm);
		if(existingUserWithPhoneNumber.size() > 0) {
			throw new InvalidRequest(ErrorConstants.USER_EXIST_WITH_PHONE_NUMBER);
		}		
		
		List<UserRepresentation> existingUserWithEmail = keycloakUtility.userByEmailAndRole(
				flightOperationManager.getEmail(), roleNameOfFOM, realm);
		if(existingUserWithEmail.size() > 0) {
			throw new InvalidRequest(ErrorConstants.EMAIL_ALREADY_EXISTS);
		}		
		return true;
	}

}
