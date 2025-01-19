package com.example.demo.serviceImpl;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.Admin;
import com.example.demo.service.AdminService;

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

	@Override
	public ResponseEntity<?> createAdmin(Admin admin, String roleNameOfAdmin, String realm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Map<String, Object>> fetchAdminList(Map<String, Object> allParams, Pageable pageable,
			String rolenameofadmin, String realm) {
		// TODO Auto-generated method stub
		return null;
	}

}
