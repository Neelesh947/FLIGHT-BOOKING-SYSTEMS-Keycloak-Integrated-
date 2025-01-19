package com.example.demo.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.example.demo.Entity.Admin;

public interface AdminService {

	ResponseEntity<?> createAdmin(Admin admin, String roleNameOfAdmin, String realm);

	Page<Map<String, Object>> fetchAdminList(Map<String, Object> allParams, Pageable pageable, String rolenameofadmin,
			String realm);

}
