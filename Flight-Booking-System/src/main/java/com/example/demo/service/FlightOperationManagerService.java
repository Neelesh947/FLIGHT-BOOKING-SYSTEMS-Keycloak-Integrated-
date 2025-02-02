package com.example.demo.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.example.vo.FlightOperationManager;

public interface FlightOperationManagerService {

	ResponseEntity<Object> createFlightOperationmanager(String adminId, FlightOperationManager flightOperationManager,
			String realm);

	@SuppressWarnings("rawtypes")
	Page<Map> fetchOperationManager(Map<String, Object> allParams, String adminId, Pageable pageable, String realm);
}
