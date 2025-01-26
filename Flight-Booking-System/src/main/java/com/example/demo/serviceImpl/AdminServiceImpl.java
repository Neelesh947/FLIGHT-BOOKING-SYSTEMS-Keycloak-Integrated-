package com.example.demo.serviceImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.Admin;
import com.example.demo.Utils.ErrorConstants;
import com.example.demo.Utils.KeycloakUtility;
import com.example.demo.config.Constants;
import com.example.demo.service.AdminService;
import com.exception.model.DataUnavailable;
import com.exception.model.InternalServerError;
import com.exception.model.InvalidRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService{
	
	private static final String IS_HIDDEN = "isHidden";
	private static final String SEARCH_STRING = "searchString";
	private static final String TRUE = "true";
//	private static final String FALSE = "false";
	private static final String ID = "id";
	private static final String LAST_NAME = "lastName";
	private static final String FIRST_NAME = "firstName";
	private static final String EMAIL = "emailId";
	private static final String PHONE_NUMBER = "phoneNumber";
	private static final String COUNTRY_CODE = "countryCode";
	private static final String TELECOM_NUMBER = "telecomnumber";
	private static final String USERNAME = "username";
	private static final String ENABLED = "enabled";
	private static final String COUNTRY = "country";
	private static final String ADDRESS = "address";
	private static final String CITY = "city";
	private static final String COMPANY_NAME = "companyName";
	private static final String ROLES = "roles";
	private static final String POSTAL_CODE = "postalCode";
	private static final String STATE = "state";
	private static final String CUSTOMER_SUPPORT_NUMBER = "customerSupportNumber";
	
	@Autowired
	private KeycloakUtility keycloakUtility;
	@Value(value = "${keycloak.security-constraints[0].authRoles[0]}")
	private String roleNameOfAdmin;

	@Override
	public ResponseEntity<?> createAdmin(Admin admin, String roleNameOfAdmin, String realm) {
		log.info("AdminServiceImpl: create Admin invoked for admin: {}", admin.getUsername());
		Map<String, String> responseEntity = null;
		Map<String, Object> responseMap = new HashMap<>();
		validateAdmin(admin, realm);
		try {
			responseEntity = keycloakUtility.createUser(admin, roleNameOfAdmin, realm);
		} catch (InternalServerError e) {
			throw new DataUnavailable(ErrorConstants.ADMIN_NOT_CREATED);
		}
		String adminId = responseEntity.get("userId");
		responseMap.put(Constants.STATUS, Constants.SUCCESS);
		responseMap.put("admin-id", adminId);
		responseMap.put(USERNAME, admin.getUsername());
		return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
	}

	@Override
	public Page<Map<String, Object>> fetchAdminList(Map<String, Object> allParams, Pageable pageable,
			String rolenameofadmin, String realm) {
		List<UserRepresentation> response = keycloakUtility.allUsersOfSpecificRoleAndRealm(rolenameofadmin, allParams ,realm);
		return getAllAdmins(allParams, pageable, response);
	}
	
	public boolean validateAdmin(Admin admin, String realm) {
		List<UserRepresentation> existingUserWithEmailAndRolesInKeycloak = keycloakUtility
				.userByEmailAndRole(admin.getEmail(), roleNameOfAdmin, realm);	
		if(!existingUserWithEmailAndRolesInKeycloak.isEmpty()) {
			log.error("User already exist with same username: {}",  admin.getUsername());
			throw new InvalidRequest(ErrorConstants.EMAIL_ALREADY_EXISTS);
		}
		List<UserRepresentation> existingUserWithUsernameInKeycloak = keycloakUtility
				.getKeycloakUserByUsername(admin.getUsername(), realm);
		if(!existingUserWithUsernameInKeycloak.isEmpty()) {
			log.error("User already exist with same username: {}",  admin.getUsername());
			throw new InvalidRequest(ErrorConstants.USERNAME_ALREADY_EXIST);
		}
		return true;
	}
	
	private PageImpl<Map<String, Object>> getAllAdmins(Map<String, Object> allParams, Pageable page, 
			List<UserRepresentation> users){
		Map<String, UserRepresentation> userWithShortNameMap = new LinkedHashMap<String, UserRepresentation>();
		users.stream().forEach(user ->{
			userWithShortNameMap.put(user.getUsername().toLowerCase(), user);
		});
		
		List<UserRepresentation> sortedList = users.stream()
					.sorted(Comparator.comparing(UserRepresentation::getCreatedTimestamp).reversed())
					.collect(Collectors.toList());
		if(sortedList.size() < 1) {
			throw new DataUnavailable(ErrorConstants.NO_DATA_FOUND);
		}
		if(allParams.containsKey(IS_HIDDEN) &&  !allParams.containsKey(SEARCH_STRING)) {
			sortedList = allParams.get(IS_HIDDEN).equals(TRUE) ? sortedList.stream()
					.filter(UserRepresentation -> UserRepresentation.isEnabled().booleanValue() == true)
					.collect(Collectors.toList()) : sortedList.stream()
					.filter(UserRepresentation -> UserRepresentation.isEnabled().booleanValue() == false)
					.collect(Collectors.toList());
		}
		if(sortedList != null && !sortedList.isEmpty()) {
			List<Map<String, Object>> allAdminList = new LinkedList<Map<String, Object>>();
			for(UserRepresentation admins : sortedList) {
				Map<String, Object> adminMaps = new LinkedHashMap<String, Object>();
				adminMaps.put(ID, admins.getId());
				if(!StringUtils.isEmpty(admins.getLastName())) {
					adminMaps.put(LAST_NAME, admins.getLastName());
				} else {
					adminMaps.put(LAST_NAME,null);
				}
				adminMaps.put(FIRST_NAME, admins.getFirstName());
				if(!StringUtils.isEmpty(admins.getEmail())) {
					adminMaps.put(EMAIL, admins.getEmail());
				}
				if(admins.getAttributes() != null && !admins.getAttributes().isEmpty()) {
					if(admins.getAttributes().containsKey(COUNTRY_CODE)) {
						adminMaps.put(COUNTRY_CODE, 
								admins.getAttributes().get(COUNTRY_CODE).stream().findAny().get());
					}
					if(admins.getAttributes().containsKey(PHONE_NUMBER)) {
						adminMaps.put(TELECOM_NUMBER, 
								admins.getAttributes().get(PHONE_NUMBER).stream().findAny().get());
					}
				}
				adminMaps.put(USERNAME, admins.getUsername());
				boolean value = userWithShortNameMap.containsKey(admins.getUsername().toLowerCase())
							? userWithShortNameMap.get(admins.getUsername()).isEnabled().booleanValue()
							: true;
				adminMaps.put(ENABLED, value);
				allAdminList.add(adminMaps);
			}
			int totalElements = allAdminList.size();
			int fromIndex = page.getPageNumber() * page.getPageSize();
			int toIndex = page.getPageSize() + fromIndex;
			return new PageImpl<>(page.getPageNumber() < Math.ceil((double) totalElements / (double) page.getPageSize())
					? allAdminList.subList(fromIndex, toIndex > totalElements ? totalElements : toIndex )
					: new ArrayList<>(), page, totalElements);
		}
		return new PageImpl<>(new ArrayList<Map<String, Object>>(), page, 0);
	}

	@Override
	public String updateAdminStatus(Map<String, Object> status, String adminId, String realm) {
		UserRepresentation userResponse = keycloakUtility.userById(adminId, realm);
		if(userResponse == null) {
			throw new DataUnavailable(ErrorConstants.NO_DATA_FOUND);
		}
		boolean noStatus = (boolean) status.get(ENABLED);
		userResponse.setEnabled(noStatus);
		keycloakUtility.updateUser(userResponse, userResponse.getId(), realm);
		return TRUE;
	}

	@Override
	public Map<String, Object> getUserById(String id, String realm) {
		UserRepresentation user = keycloakUtility.userById(id, realm);
		if(user == null) {
			throw new DataUnavailable(ErrorConstants.ADMIN_NOT_FOUND);
		}
		Map<String, Object> admin = new LinkedHashMap<String, Object>();
		admin.put(ID, user.getId());
		admin.put(FIRST_NAME, user.getFirstName());
		admin.put(LAST_NAME, user.getLastName());
		admin.put(USERNAME, user.getUsername());
		admin.put(EMAIL, user.getEmail());
		admin.put(PHONE_NUMBER, user.getAttributes().get(PHONE_NUMBER));
		admin.put(COUNTRY_CODE, user.getAttributes().get(COUNTRY_CODE));
		admin.put(TELECOM_NUMBER, user.getAttributes().get(CUSTOMER_SUPPORT_NUMBER));
		admin.put(COUNTRY, user.getAttributes().get(COUNTRY));
		admin.put(ADDRESS, user.getAttributes().get(ADDRESS));
		admin.put(CITY, user.getAttributes().get(CITY));
		admin.put(COMPANY_NAME, user.getAttributes().get(COMPANY_NAME));
		admin.put(ROLES, user.getAttributes().get(ROLES));
		admin.put(POSTAL_CODE, user.getAttributes().get(POSTAL_CODE));
		admin.put(STATE, user.getAttributes().get(STATE));
		return admin;
	}

	@Override
	public ResponseEntity<?> updateAdmins(Admin admin, String adminId, String realm) {
		log.info("update admin method invoked adminId: {}", adminId);
		UserRepresentation userRepresentation = keycloakUtility.getKeycloakUserByUsername(admin.getUsername(), realm).get(0);
		Map<String, List<String>> attributeList = userRepresentation.getAttributes();
		
		List<UserRepresentation> userList = keycloakUtility.usersByPhoneAndRole(admin.getPhoneNumber(), roleNameOfAdmin, realm);
		if (userList !=null) {
		    boolean isSameUser = userList.stream().anyMatch(user -> adminId.equals(user.getId()));		    
		    if (!isSameUser) {
		        throw new InvalidRequest(ErrorConstants.USER_EXIST_WITH_PHONE_NUMBER);
		    }
		}

		if(admin.getPhoneNumber() != null) {
			if(admin.getPhoneNumber().replaceAll("\\s+", "").matches("\\d*") == false) {
				log.info("AdminServiceImpl: update admin | Invalid phone number for admin: {}", admin.getUsername());
				throw new InvalidRequest(ErrorConstants.INVALID_PHONE_NUMBER);
			}
			ArrayList<String> phoneNumber = new ArrayList<>();
			phoneNumber.add(admin.getPhoneNumber());
			attributeList.put(PHONE_NUMBER, phoneNumber);
			userRepresentation.setAttributes(attributeList);
		}
		
		if (admin.getEmail() != null) {
		    if (!Pattern.matches(Constants.EMAIL_REGIX, admin.getEmail())) {
		        log.info("AdminServiceImpl: update admin | Invalid email id for admin: {}", admin.getUsername());
		        throw new InvalidRequest(ErrorConstants.INVALID_EMAIL);
		    }		    
		    List<UserRepresentation> existingEmailWithUsername = keycloakUtility.userByEmailAndRole(
		    		admin.getEmail(), roleNameOfAdmin, realm);
		    
		    if (existingEmailWithUsername.stream().anyMatch(u -> !adminId.equals(u.getId()))) {
		        throw new InvalidRequest(ErrorConstants.EMAIL_ALREADY_EXISTS);
		    }
		}

		
		userRepresentation.setEmail(admin.getEmail() != null ? admin.getEmail() : userRepresentation.getEmail());
		if(admin.getCustomerSupportNumber() != null) {
			if(admin.getCustomerSupportNumber().replace("\\s+", "").matches("\\d*") == false) {
				log.error("invalid phone number for admin: {} ", admin.getUsername());
				throw new InvalidRequest(ErrorConstants.INVALID_PHONE_NUMBER);
			}
			ArrayList<String> customerSupportNumber = new ArrayList<>();
			customerSupportNumber.add(admin.getCustomerSupportNumber());
			attributeList.put(CUSTOMER_SUPPORT_NUMBER, customerSupportNumber);
			userRepresentation.setAttributes(attributeList);
		}
		if(admin.getAddress() != null) {
			ArrayList<String> address = new ArrayList<>();
			address.add(admin.getAddress());
			attributeList.put(ADDRESS, address);
			userRepresentation.setAttributes(attributeList);
		}
		if(admin.getPostalCode() != null) {
			ArrayList<String> postalCode = new ArrayList<>();
			postalCode.add(admin.getPostalCode());
			attributeList.put(POSTAL_CODE, postalCode);
			userRepresentation.setAttributes(attributeList);
		}
		if(admin.getCity() != null) {
			ArrayList<String> city = new ArrayList<>();
			city.add(admin.getCity());
			attributeList.put(CITY, city);
			userRepresentation.setAttributes(attributeList);
		}
		if(admin.getState() != null) {
			ArrayList<String> state = new ArrayList<>();
			state.add(admin.getState());
			attributeList.put(STATE, state);
			userRepresentation.setAttributes(attributeList);
		}
		if(admin.getCountry() != null) {
			ArrayList<String> country = new ArrayList<>();
			country.add(admin.getCountry());
			attributeList.put(COUNTRY, country);
			userRepresentation.setAttributes(attributeList);
		}
		if(admin.getFirstName() != null) {
			userRepresentation.setFirstName(admin.getFirstName());
		}
		if(admin.getLastName() != null) {
			userRepresentation.setLastName(admin.getLastName());
		}
		userRepresentation.setEnabled(admin.isEnabled());
		keycloakUtility.updateUser(userRepresentation, userRepresentation.getId(), realm);
		return ResponseEntity.ok(new UserRepresentation());
	}
}
