package com.keycloak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.keycloak","com.exception.handler"})
public class KeyCloakProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeyCloakProxyApplication.class, args);
	}

}
