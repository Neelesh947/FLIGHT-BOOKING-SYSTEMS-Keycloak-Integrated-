package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entity.Admin;
import com.example.demo.service.AdminService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/{realm}/admin")
public class AdminController {
	
	private String roleNameOfAdmin = "admin";
	
	@Autowired
	private AdminService adminService;

	@PostMapping
	public ResponseEntity<?> createAdmin(@RequestBody Admin admin, @PathVariable String realm){
		log.info("Create admin");
		try {
			ResponseEntity<?> createAdmin = adminService.createAdmin(admin, roleNameOfAdmin, realm);
			return ResponseEntity.status(HttpStatus.CREATED).body(createAdmin);
		} catch (Exception e) {
			throw e;
		}
	}
}
