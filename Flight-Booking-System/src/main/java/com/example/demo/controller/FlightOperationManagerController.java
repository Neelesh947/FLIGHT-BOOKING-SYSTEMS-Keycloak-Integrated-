package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.FlightOperationManagerService;
import com.example.utils.SecurityUtils;
import com.example.vo.FlightOperationManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/{realm}/FlightOperationManager/")
public class FlightOperationManagerController {
	
	@Autowired
	private FlightOperationManagerService flightOperationManagerService;

	/**
	 * Create Flight operationManager
	 * @param flightOperationManager
	 * @param realm
	 * @return
	 */
	@PostMapping("/create/operation-manager")
	public ResponseEntity<Object> createFlightOperationManager(@RequestBody FlightOperationManager 
			flightOperationManager, @PathVariable String realm) {
		log.info("creating operation manager: {}", flightOperationManager.getUsername());
		String adminId = SecurityUtils.getCurrentUserIdSupplier.get();
		ResponseEntity<Object> operationManager = flightOperationManagerService.createFlightOperationmanager(
				adminId, flightOperationManager, realm); 
		return operationManager;
	}
	
}
