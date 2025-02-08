package com.example.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entity.User;
import com.example.demo.Utils.ErrorConstants;
import com.example.demo.Utils.SecurityUtils;
import com.example.demo.service.UserService;
import com.exception.model.InternalServerError;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/{realm}/user")
public class UserController {

	@Autowired
	private UserService userService;
	
	/**
	 * create user
	 * @param user
	 * @param realm
	 * @return
	 */
	@PostMapping
	public ResponseEntity<?> createUser(@RequestBody User user, @PathVariable String realm) {
		log.info("invoked create user: {}", user);
		String flightManagerOperationId = SecurityUtils.getCurrentUserIdSupplier.get();
		ResponseEntity<Object> response = userService.createUser(user, flightManagerOperationId, realm);
		return response;
	}
	
	/**
	 * Get the list of users
	 * @param allParams
	 * @param pageable
	 * @param realm
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@GetMapping
	public ResponseEntity<?> getListOfUsers(@RequestParam Map<String, Object> allParams, Pageable pageable, 
				@PathVariable String realm) {
		log.info("invoked the method get list for the users");
		String flightManagerOperationId = SecurityUtils.getCurrentUserIdSupplier.get();
		try {
			Page<Map> userList = userService.fetchListOfUsers(allParams, flightManagerOperationId, pageable, realm);
			if (userList.isEmpty()) {
                log.info("No flight operation manager found for the given parameters.");
                return ResponseEntity.noContent().build();
            }
            log.info("Successfully fetched flight operation manager list for realm: {}", realm);
            return ResponseEntity.ok(userList);
		} catch(Exception e) {
			log.error("Error while fetching users list: {}", e.getMessage(), e);
            throw new InternalServerError(ErrorConstants.INTERNAL_SERVER_ERROR);
		}
	}
}
