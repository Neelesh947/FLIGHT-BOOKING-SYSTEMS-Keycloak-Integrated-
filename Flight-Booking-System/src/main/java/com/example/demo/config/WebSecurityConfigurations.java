package com.example.demo.config;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfigurations {
	
	private static final int SEGMENT_INDEX = 1;
	
	@Value("${keycloak.initial.jwks.url}")
	private String initialJwksUrl;
	
	@Value("${keycloak.final.jwks.url}")
	private String remainingJwksUrl;
	
	@Autowired
	private CustomAuthorizationErrorHandler errorHandler;
	
	/**
	 * Function is responsible to extract realm from request URI
	 * and return the keyCloak URL to get the public certificate specific to the realm
	 */
	private Function<HttpServletRequest, String> jwksUriExtractor = request -> {
		String [] pathSegments = request.getRequestURI().split("/");
		return initialJwksUrl + pathSegments[SEGMENT_INDEX] + remainingJwksUrl;
	};

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configure(http)) // Enable CORS using the new DSL
                .csrf(csrf -> csrf.disable()) // Disable CSRF using the new DSL
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/**/FlightOperationManager/**").hasAuthority("flight_operations_manager")		
	            .anyRequest().permitAll() // Allow all requests (adjust as needed)
                )
                .oauth2ResourceServer(server -> server
                		.jwt(jwt -> jwt
                                .decoder(jwtDecoder()) // JWT Decoder
                                .jwtAuthenticationConverter(jwtAuthenticationProvider())  // JWT Authentication Converter
                            )
                        )
                .exceptionHandling(handling -> handling
                        .accessDeniedHandler(errorHandler));
	    return http.build();
	}


	
	@Bean
    public JwtDecoder jwtDecoder() {
		return token -> {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
					.getRequest();
			String jwksUri = jwksUriExtractor.apply(request);
			System.out.println("Result is: ="+ jwksUri);
			return NimbusJwtDecoder.withJwkSetUri(jwksUri).build().decode(token);
		};
	}
	
	@Bean
    JwtAuthenticationConverter jwtAuthenticationProvider() {
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(jwt ->{
			List<String> roles = ExtractAuthorityFromJwt.extractRoles(jwt.getClaim("realm_access"));
			return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
		});		
		
		return converter;
	}
	
	public CustomAuthenticationConverterForClaims authenticationConverterForClaims() {
		return new CustomAuthenticationConverterForClaims();
	}
}
