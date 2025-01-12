package com.example.demo.Entity;

import com.keycloak.util.KeyCloakRepresentation;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admin")
public class Admin extends KeyCloakRepresentation{

	private String id;
}
