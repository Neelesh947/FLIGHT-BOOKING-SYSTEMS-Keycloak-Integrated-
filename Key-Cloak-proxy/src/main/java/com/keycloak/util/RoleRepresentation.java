package com.keycloak.util;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class RoleRepresentation {

    private String id;
	
	private String name;
	
	private String description;
	
	private boolean composite;
	
	private boolean clientRole;
	
	private String containerId;
	
	private Map<String, List<String>> attributes;
}
