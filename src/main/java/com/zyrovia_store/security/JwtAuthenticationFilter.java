package com.zyrovia_store.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	// JWT utility class
	private final JwtUtils jwtUtils;

	// Loads user details from database
	private final CustomUserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// Read Authorization header
		String authHeader = request.getHeader("Authorization");

		// If header is missing or invalid, continue request
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {

			filterChain.doFilter(request, response);
			return;
		}

		// Extract JWT token
		String token = authHeader.substring(7);

		// Extract email from JWT
		String email = this.jwtUtils.extractEmail(token);

		// Authenticate only if user is not already authenticated
		if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			// Create an Authentication object with authenticated user details
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

			// Validate JWT
			if (this.jwtUtils.validateToken(token, userDetails.getUsername())) {

				UsernamePasswordAuthenticationToken authentication = 
						new UsernamePasswordAuthenticationToken(
						userDetails,                     // Authenticated user
						null, 							// No credentials required after authentication
						userDetails.getAuthorities());  // User roles/authorities
				
			    // Attach additional request information (IP address, session ID, etc.)
				authentication.setDetails(
						new WebAuthenticationDetailsSource()
						.buildDetails(request));
				
			    // Store authentication object in SecurityContext
			    // so Spring Security treats the user as authenticated
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}

		// Continue request
		filterChain.doFilter(request, response);
	}
}
