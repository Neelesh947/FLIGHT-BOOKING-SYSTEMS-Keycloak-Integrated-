package com.example.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entity.Admin;
import com.example.demo.Utils.ErrorConstants;
import com.example.demo.service.AdminService;
import com.exception.model.InternalServerError;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/{realm}/admin")
public class AdminController {
	
	@Value(value = "${keycloak.security-constraints[0].authRoles[0]}")
	private String roleNameOfAdmin;
	
	@Autowired
	private AdminService adminService;

	@PostMapping("/create-admin")
	public ResponseEntity<?> createAdmin(@RequestBody Admin admin, @PathVariable String realm){
		log.info("Create admin");
		try {
			ResponseEntity<?> createAdmin = adminService.createAdmin(admin, roleNameOfAdmin, realm);
			return ResponseEntity.status(HttpStatus.CREATED).body(createAdmin);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@GetMapping("/get-admin-list")
	public ResponseEntity<?> getListOfAdmins(@RequestParam Map<String, Object> allParams,
			Pageable pageable, @PathVariable String realm){
		log.info("Getting the list of Admins");
		try {
			Page<Map<String, Object>> adminList = adminService.fetchAdminList(allParams, pageable, roleNameOfAdmin, realm);
			if (adminList.isEmpty()) {
                log.info("No admins found for the given parameters.");
                return ResponseEntity.noContent().build();
            }
            log.info("Successfully fetched admin list for realm: {}", realm);
            return ResponseEntity.ok(adminList);
		} catch (Exception e) {
            log.error("Error while fetching admin list: {}", e.getMessage(), e);
            throw new InternalServerError(ErrorConstants.INTERNAL_SERVER_ERROR);
        }
	}
}
