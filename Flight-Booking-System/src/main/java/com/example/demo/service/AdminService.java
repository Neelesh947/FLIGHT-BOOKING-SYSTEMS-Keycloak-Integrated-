package com.example.demo.service;

import org.springframework.http.ResponseEntity;

import com.example.demo.Entity.Admin;

public interface AdminService {

	ResponseEntity<?> createAdmin(Admin admin, String roleNameOfAdmin, String realm);

}
