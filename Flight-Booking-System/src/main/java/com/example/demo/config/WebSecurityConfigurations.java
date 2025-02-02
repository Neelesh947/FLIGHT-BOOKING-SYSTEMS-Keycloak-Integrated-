//package com.example.demo.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class WebSecurityConfigurations {
//
//	@Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .cors(cors -> cors.configure(http)) // Enable CORS using the new DSL
//            .csrf(csrf -> csrf.disable()) // Disable CSRF using the new DSL
//            .authorizeHttpRequests(auth -> auth
//                .anyRequest().permitAll() // Allow all requests (adjust as needed)
//            );
//        return http.build();
//    }
//}
