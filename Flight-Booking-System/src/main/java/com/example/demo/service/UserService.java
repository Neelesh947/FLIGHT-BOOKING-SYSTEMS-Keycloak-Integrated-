package com.example.demo.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.example.demo.Entity.User;

public interface UserService {

	ResponseEntity<Object> createUser(User user, String flightManagerOperationId, String realm);

	Page<Map> fetchListOfUsers(Map<String, Object> allParams, String flightManagerOperationId, Pageable pageable,
			String realm);

}
