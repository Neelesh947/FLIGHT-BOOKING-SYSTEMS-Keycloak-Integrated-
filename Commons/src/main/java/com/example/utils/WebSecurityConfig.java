//package com.example.utils;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class WebSecurityConfig {
//
//	@Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf().disable()
//            .authorizeRequests()
//                .anyRequest().authenticated()  // All requests need to be authenticated
//            .and()
//            .oauth2ResourceServer()
//                .jwt()  // Configure OAuth2 resource server with JWT support
//                .jwtAuthenticationConverter(jwtAuthenticationConverter()); // Custom converter (if necessary)
//        return http.build();
//    }
//
//    @Bean
//    public JwtDecoder jwtDecoder() {
//        // Using NimbusJwtDecoder to decode and validate JWTs using the Keycloak's JWKS URI
//        return NimbusJwtDecoder.withJwkSetUri("https://<keycloak-server>/realms/<realm-name>/protocol/openid-connect/certs")
//                                .build();
//    }
//
//    @Bean
//    public JwtAuthenticationConverter jwtAuthenticationConverter() {
//        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
//        // Optionally, you can set a custom authority converter here if you want to extract roles or other claims
//        return converter;
//    }
//}
