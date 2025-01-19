package com.example.demo.serviceImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.Admin;
import com.example.demo.Utils.ErrorConstants;
import com.example.demo.Utils.KeycloakUtility;
import com.example.demo.service.AdminService;
import com.exception.model.DataUnavailable;
import com.exception.model.InvalidRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService{
	
	private static final String IS_HIDDEN = "isHidden";
	private static final String SEARCH_STRING = "searchString";
	private static final String TRUE = "true";
	private static final String FALSE = "false";
	private static final String ID = "id";
	private static final String LAST_NAME = "lastName";
	private static final String FIRST_NAME = "firstName";
	private static final String EMAIL = "emailId";
	private static final String PHONE_NUMBER = "phoneNumber";
	private static final String COUNTRY_CODE = "countryCode";
	private static final String TELECOM_NUMBER = "telecomnumber";
	private static final String USERNAME = "username";
	private static final String ENABLED = "enabled";
	
	@Autowired
	private KeycloakUtility keycloakUtility;
	@Value(value = "${keycloak.security-constraints[0].authRoles[0]}")
	private String roleNameOfAdmin;

	@Override
	public ResponseEntity<?> createAdmin(Admin admin, String roleNameOfAdmin, String realm) {
		log.info("AdminServiceImpl: create Admin invoked for admin: {}", admin.getUsername());
//		KeyCloakRepresentation response = null;
//		validateAdmin(admin, realm);
//		try {
//			response = keycloakService.createKeycloakUsersAndAssignRoles(admin, realm, realm, roleNameOfAdmin);
//		} catch (InternalServerError e) {
//			throw new DataUnavailable(Constants.ADMIN_NOT_CREATED);
//		}
		return null;
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
					.collect(Collectors.toList()) : sortedList ;
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
}
