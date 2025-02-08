package com.example.demo.serviceImpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
import com.example.demo.Entity.FlightManagerOperationAndUserMapping;
import com.example.demo.Entity.User;
import com.example.demo.Utils.Constants;
import com.example.demo.Utils.ErrorConstants;
import com.example.demo.Utils.KeycloakUtility;
import com.example.demo.repository.FlightManagerOperationAndUserMappingRepository;
import com.example.demo.service.UserService;
import com.exception.model.DataUnavailable;
import com.exception.model.InternalServerError;
import com.exception.model.InvalidRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	private KeycloakUtility keycloakUtility;
	
	@Value("${keycloak.security-constraints[0].authRoles[1]}")
	private String roleNameOfUser;
	
	ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	private FlightManagerOperationAndUserMappingRepository flightManagerOperationAndUserMappingRepository;

	@Override
	public ResponseEntity<Object> createUser(User user, String flightManagerOperationId, String realm) {
		log.info("user creation started..");
		validateUser(user, flightManagerOperationId, realm);
		try {
			Map<String, String> response = keycloakUtility.createUser(user, roleNameOfUser, realm);
			String userString = response.get("userId").toString();
			JsonNode rootNode = mapper.readTree(userString);
			String userId = rootNode.path("userId").asText();
			if(user == null) {
				throw new DataUnavailable(ErrorConstants.NOT_FOUND);
			}
			FlightManagerOperationAndUserMapping mappings = createFlightOperationManagerAndUserMappings(
					flightManagerOperationId, userId);
			flightManagerOperationAndUserMappingRepository.save(mappings);
			return ResponseEntity.status(HttpStatus.CREATED).body(userString);
		} catch (Exception e) {
			log.error("some exception occurred", e.getMessage());
			throw new InternalServerError(ErrorConstants.INTERNAL_SERVER_ERROR);
		}
	}
	
	private FlightManagerOperationAndUserMapping createFlightOperationManagerAndUserMappings(
			String flightManagerOperationId, String userId) {
		FlightManagerOperationAndUserMapping map = new FlightManagerOperationAndUserMapping();
		map.setUserId(userId);
		map.setFlightmanagerId(flightManagerOperationId);
		map.setCreateDateTime(Timestamp.valueOf(LocalDateTime.now()));
		map.setUpdateDateTime(Timestamp.valueOf(LocalDateTime.now()));
		map.setId(UUID.randomUUID().toString());
		return map;
	}

	private boolean validateUser(User user, String flightManagerOperationId, String realm) {
		if(user.getUsername() == null || user.getUsername().isEmpty()) {
			throw new InvalidRequest(ErrorConstants.USERNAME_CANNOT_BE_EMPTY);
		}
		if(user.getPassword() == null || user.getPassword().isEmpty()) {
			throw new InvalidRequest(ErrorConstants.PASSWORD_CANNOT_BE_EMPTY);
		}
		if(user.getEmail() == null || user.getEmail().isEmpty()) {
			throw new InvalidRequest(ErrorConstants.EMAIL_CANNOT_BE_EMPTY);
		}
		List<UserRepresentation> existingUserWithPhoneNumber = keycloakUtility.usersByPhoneAndRole(
				user.getPhoneNumber(), roleNameOfUser, realm);
//		if(existingUserWithPhoneNumber.size() > 0) {
//			throw new InvalidRequest(ErrorConstants.USER_EXIST_WITH_PHONE_NUMBER);
//		}				
		List<UserRepresentation> existingUserWithEmail = keycloakUtility.userByEmailAndRole(
				user.getEmail(), roleNameOfUser, realm);
		if(existingUserWithEmail.size() > 0) {
			throw new InvalidRequest(ErrorConstants.EMAIL_ALREADY_EXISTS);
		}	
		return true;
	}

	/**
	 * get the list of users
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Page<Map> fetchListOfUsers(Map<String, Object> allParams, String flightManagerOperationId, Pageable page,
			String realm) {
		log.info("Fetch user list invoked");
		try {
			List<UserRepresentation> fetchListOfUsers = keycloakUtility.allUsersOfSpecificRoleAndRealm(
					roleNameOfUser, allParams, realm);
			List<FlightManagerOperationAndUserMapping> listofMappedUser = findTheListOfLinkedUser(flightManagerOperationId);
			List<UserRepresentation> linkedUser = fetchListOfUsers
									.stream()
									.filter(listOfUser -> listofMappedUser.stream()
											.anyMatch(mapping -> mapping.getUserId().equals(listOfUser.getId())))
									.collect(Collectors.toList());
			List<UserRepresentation> sortedList = linkedUser.stream()
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
			List<Map> userLists = sortedList.stream().map(user -> {
				Map<String, Object> map = generateUserMap(user);
				return map;
			}).collect(Collectors.toList());
			
			int totalElements = userLists.size();
			int fromIndex = page.getPageNumber() * page.getPageSize();
			int toIndex = page.getPageSize() + fromIndex;
			return new PageImpl<>(page.getPageNumber() < Math.ceil((double) totalElements / (double) page.getPageSize())
					? userLists.subList(fromIndex, toIndex > totalElements ? totalElements : toIndex )
					: new ArrayList<>(), page, totalElements);
		} catch(Exception e) {
			log.info("Some exception occurred", e.getMessage() , e);
			throw new InternalServerError(ErrorConstants.INTERNAL_SERVER_ERROR);
		}
	}

	private Map<String, Object> generateUserMap(UserRepresentation user) {
		Map<String, Object> map = new HashMap<>();
		map.put(Constants.ID, user.getId());
		map.put(Constants.USERNAME, user.getUsername());
		map.put(Constants.FIRST_NAME, user.getFirstName());
		map.put(Constants.LAST_NAME, user.getLastName());
		map.put(Constants.ENABLED, user.isEnabled());
		map.put(Constants.EMAIL, user.getEmail());
		map.put(Constants.PHONE_NUMBER,
				user.getAttributes().containsKey(Constants.PHONE_NUMBER)
					? user.getAttributes().get(Constants.PHONE_NUMBER).iterator().next()
							: "N/A");
		map.put(Constants.COUNTRY_CODE,
				user.getAttributes().containsKey(Constants.COUNTRY_CODE)
					? user.getAttributes().get(Constants.COUNTRY_CODE).iterator().next()
							: "N/A");
		return map;
	}
	
	private List<FlightManagerOperationAndUserMapping> findTheListOfLinkedUser(String flightManagerOperationId) {
		List<FlightManagerOperationAndUserMapping> list = flightManagerOperationAndUserMappingRepository
										.findByFlightmanagerId(flightManagerOperationId);
		return list;
	}

}
