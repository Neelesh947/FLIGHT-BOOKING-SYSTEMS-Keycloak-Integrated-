package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Utils.ErrorConstants;
import com.example.demo.config.Constants;
import com.example.demo.service.FlightOperationManagerService;
import com.example.utils.SecurityUtils;
import com.example.vo.FlightOperationManager;
import com.exception.model.InternalServerError;

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
	
	/**
	 * get list of flight operation manager
	 * @param allParams
	 * @param pageAble
	 * @param realm
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@GetMapping("/get-flight-operation-manager")
	public ResponseEntity<?> getListOfFlightOperationManager(@RequestParam Map<String, Object> allParams,
			Pageable pageable, @PathVariable String realm) {
		log.info("getting the list of flight-operation-manager");
		String adminId = SecurityUtils.getCurrentUserIdSupplier.get();
		try {
			Page<Map> operationManagerList = flightOperationManagerService.fetchOperationManager(
					allParams, adminId, pageable, realm);
			if (operationManagerList.isEmpty()) {
                log.info("No flight operation manager found for the given parameters.");
                return ResponseEntity.noContent().build();
            }
            log.info("Successfully fetched flight operation manager list for realm: {}", realm);
            return ResponseEntity.ok(operationManagerList);
		} catch (Exception e) {
            log.error("Error while fetching flight operation manager list: {}", e.getMessage(), e);
            throw new InternalServerError(ErrorConstants.INTERNAL_SERVER_ERROR);
        }
	}
	
	/**
	 * Update the status of FOM : - active or inactive
	 * @param status
	 * @param fomId
	 * @param realm
	 * @return
	 */
	@PatchMapping("/updateStatus/{id}")
	public ResponseEntity<?> updateFlightOperationManagerStatus(@RequestBody Map<String, Object> status, 
			@PathVariable("id") String fomId, @PathVariable String realm) {
		log.info("Invoked updateFlightOperationManager for Admin {} with request body: {}", fomId, status.toString());
		String response = flightOperationManagerService.updateFlightOperationManagerStatus(status, fomId, realm);
		Map<String, Object> resMap = new HashMap<>();
		if(response.equals(Constants.TRUE)) {
			resMap.put(Constants.STATUS, Constants.SUCCESS);
			resMap.put(Constants.MESSAGE, Constants.SUCCESSFULLY_UPDATED);
			return new ResponseEntity<>(resMap, HttpStatus.OK);
		}
		return null;
	}
}
