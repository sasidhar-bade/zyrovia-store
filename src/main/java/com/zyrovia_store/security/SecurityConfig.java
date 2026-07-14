package com.zyrovia_store.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	// Custom JWT Authentication Filter
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	// Configure Spring Security Filter Chain
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
		
			// Disable CSRF for REST APIs
			.csrf(csrf -> csrf.disable())
			
			// StateLess session because JWT is used
			.sessionManagement(session -> session
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			
			// Authorization rules
			.authorizeHttpRequests(auth -> auth
					
					// Public APIs
					.requestMatchers("/auth/**").permitAll()
					.requestMatchers(HttpMethod.POST,"/users").permitAll()
					.requestMatchers( 
							"/v3/api-docs/**", 
							"/swagger-ui/**", 
							"/swagger-ui.html")
					.permitAll()
					
					// Secure remaining APIs
					.anyRequest()
					.authenticated())
			
			// HTTP Basic disabled
			.httpBasic(httpBasic -> httpBasic.disable());

		// Add JWT filter before UsernamePasswordAuthenticationFilter
		http.addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	// Password Encoder Bean
	@Bean
	PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
	}

	// Authentication Manager Bean
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

		return configuration.getAuthenticationManager();
	}

}
