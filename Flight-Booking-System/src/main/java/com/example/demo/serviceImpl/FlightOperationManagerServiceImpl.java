package com.example.demo.serviceImpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.AdminAndFlightManagerMapping;
import com.example.demo.Entity.FlightOperationManager;
import com.example.demo.Utils.Constants;
import com.example.demo.Utils.ErrorConstants;
import com.example.demo.Utils.KeycloakUtility;
import com.example.demo.repository.AdminAndFlightManagerMappingRepository;
import com.example.demo.service.FlightOperationManagerService;
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
//		if(existingUserWithPhoneNumber.size() > 0) {
//			throw new InvalidRequest(ErrorConstants.USER_EXIST_WITH_PHONE_NUMBER);
//		}		
		
		List<UserRepresentation> existingUserWithEmail = keycloakUtility.userByEmailAndRole(
				flightOperationManager.getEmail(), roleNameOfFOM, realm);
		if(existingUserWithEmail.size() > 0) {
			throw new InvalidRequest(ErrorConstants.EMAIL_ALREADY_EXISTS);
		}		
		return true;
	}

	/**
	 * get list of operation manager based on adminId
	 */
	@SuppressWarnings("rawtypes")
	public Page<Map> fetchOperationManager(Map<String, Object> allParams, String adminId, Pageable page,
			String realm) {
		log.info("Invoked operation manager");
		try {
			List<UserRepresentation> flightOperations = keycloakUtility.allUsersOfSpecificRoleAndRealm(
					roleNameOfFOM, allParams, realm);
			List<AdminAndFlightManagerMapping> listOfMappedFOM = findTheListOfLinkedFOM(adminId);
			List<UserRepresentation> linkedFlightOperationManager = flightOperations
						.stream()
						.filter(flightOperation -> listOfMappedFOM.stream()
								.anyMatch(mapping -> mapping.getFlightmanagerId().equals(flightOperation.getId())))
						.collect(Collectors.toList());
			List<UserRepresentation> sortedList = linkedFlightOperationManager.stream()
						.sorted(Comparator.comparing(UserRepresentation :: getCreatedTimestamp).reversed())
						.collect(Collectors.toList());
			if(sortedList.size() < 1) {
				throw new DataUnavailable(ErrorConstants.NO_DATA_FOUND);
			}
			if(allParams.containsKey(Constants.IS_HIDDEN) &&  !allParams.containsKey(Constants.SEARCH_STRING)) {
				sortedList = allParams.get(Constants.IS_HIDDEN).equals(Constants.TRUE) ? sortedList.stream()
						.filter(UserRepresentation -> UserRepresentation.isEnabled().booleanValue() == true)
						.collect(Collectors.toList()) : sortedList.stream()
						.filter(UserRepresentation -> UserRepresentation.isEnabled().booleanValue() == false)
						.collect(Collectors.toList());
			}
			List<Map> flightManagerList = sortedList.stream().map(manager -> {
				Map<String, Object> map = generateFlightManagerMap(manager);
				return map;
			}).collect(Collectors.toList());
			int totalElements = flightManagerList.size();
			int fromIndex = page.getPageNumber() * page.getPageSize();
			int toIndex = page.getPageSize() + fromIndex;
			return new PageImpl<>(page.getPageNumber() < Math.ceil((double) totalElements / (double) page.getPageSize())
					? flightManagerList.subList(fromIndex, toIndex > totalElements ? totalElements : toIndex )
					: new ArrayList<>(), page, totalElements);
		} catch(Exception e) {
			log.info("Some exception occurred", e.getMessage() , e);
			throw new InternalServerError(ErrorConstants.INTERNAL_SERVER_ERROR);
		}
	}

	private Map<String, Object> generateFlightManagerMap(UserRepresentation manager) {
		Map<String, Object> map = new HashMap<>();
		map.put(Constants.ID, manager.getId());
		map.put(Constants.USERNAME, manager.getUsername());
		map.put(Constants.FIRST_NAME, manager.getFirstName());
		map.put(Constants.LAST_NAME, manager.getLastName());
		map.put(Constants.ENABLED, manager.isEnabled());
		map.put(Constants.EMAIL, manager.getEmail());
		map.put(Constants.PHONE_NUMBER,
				manager.getAttributes().containsKey(Constants.PHONE_NUMBER)
					? manager.getAttributes().get(Constants.PHONE_NUMBER).iterator().next()
							: "N/A");
		map.put(Constants.COUNTRY_CODE,
				manager.getAttributes().containsKey(Constants.COUNTRY_CODE)
					? manager.getAttributes().get(Constants.COUNTRY_CODE).iterator().next()
							: "N/A");
		return map;
	}

	private List<AdminAndFlightManagerMapping> findTheListOfLinkedFOM(String adminId) {
		List<AdminAndFlightManagerMapping> list = adminAndFlightManagerMappingRepository
							.findByAdminId(adminId);
		return list;
	}

	/**
	 * status active and inactive
	 */
	public String updateFlightOperationManagerStatus(Map<String, Object> status, String fomId, String realm) {
		UserRepresentation userResponse = keycloakUtility.userById(fomId, realm);
		if(userResponse == null) {
			throw new DataUnavailable(ErrorConstants.NO_DATA_FOUND);
		}
		boolean enabled = (boolean) status.get(Constants.ENABLED);
		userResponse.setEnabled(enabled);
		keycloakUtility.updateUser(userResponse, userResponse.getId(), realm);
		return Constants.TRUE;
	}

	/**
	 * get flight operation manager by id
	 */
	public Map<String, Object> getUserById(String id, String realm) {
		UserRepresentation user = keycloakUtility.userById(id, realm);
		if(user == null ) {
			throw new DataUnavailable(ErrorConstants.NO_DATA_FOUND);
		}
		Map<String, Object> flightOperationManager = new LinkedHashMap<>();
		flightOperationManager.put(Constants.ID, user.getId());
		flightOperationManager.put(Constants.EMAIL, user.getEmail());
		flightOperationManager.put(Constants.FIRST_NAME, user.getFirstName());
		flightOperationManager.put(Constants.LAST_NAME, user.getLastName());
		flightOperationManager.put(Constants.PHONE_NUMBER, user.getAttributes().get(Constants.PHONE_NUMBER));
		flightOperationManager.put(Constants.USERNAME, user.getUsername());
		flightOperationManager.put(Constants.COUNTRY_CODE, user.getAttributes().get(Constants.COUNTRY_CODE));
		flightOperationManager.put(Constants.COUNTRY, user.getAttributes().get(Constants.COUNTRY));
		flightOperationManager.put(Constants.ADDRESS, user.getAttributes().get(Constants.ADDRESS));
		flightOperationManager.put(Constants.CITY, user.getAttributes().get(Constants.CITY));
		flightOperationManager.put(Constants.POSTAL_CODE, user.getAttributes().get(Constants.POSTAL_CODE));
		flightOperationManager.put(Constants.STATE, user.getAttributes().get(Constants.STATE));
		return flightOperationManager;
	}

	@Override
	public ResponseEntity<?> updateFlightOperationManager(FlightOperationManager flightOperationManager, String fomId,
			String realm) {
		// TODO Auto-generated method stub
		return null;
	}

}
