package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.utils.SecurityUtils;
import com.example.vo.FlightOperationManager;

@RestController
@RequestMapping("/{realm}/FlightOperationManager/")
public class FlightOperationManagerController {

	@PostMapping("/create/operation-manager")
	public ResponseEntity<Object> createFlightOperationManager(@RequestBody FlightOperationManager 
			flightOperationManager, @PathVariable String realm) {
		String adminId = SecurityUtils.getCurrentUserIdSupplier.get();
		return null;
	}
	
}
