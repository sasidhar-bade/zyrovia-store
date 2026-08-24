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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	// Custom JWT Authentication Filter
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	
	// Handles 401 Unauthorized and 403 Forbidden responses
	private final SecurityExceptionHandler securityExceptionHandler;

	// Configure Spring Security Filter Chain
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
		
			// Disable CSRF for REST APIs
			.csrf(csrfCustomzier -> csrfCustomzier.disable())
			
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
			
			// Handle 401 and 403 errors
			.exceptionHandling(exception -> exception
					.authenticationEntryPoint(securityExceptionHandler)
					.accessDeniedHandler(securityExceptionHandler))
			
			// HTTP Basic disabled
			.httpBasic(httpBasic -> httpBasic.disable());

		// Add JWT filter before UsernamePasswordAuthenticationFilter
		http.addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	// Password Encoder Bean
	@Bean
	BCryptPasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
	}

	// Authentication Manager Bean
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

		return configuration.getAuthenticationManager();
	}

}
