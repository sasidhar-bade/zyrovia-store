package com.zyrovia_store.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler{

	// Handles authenticated users without sufficient permissions
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType("application/json");
		
		response.getWriter().write("""
				{
					"status": 403,
					"error": "Forbidden",
					"message": "Access Denied"
				}
				""");
	}

	// Handles unauthenticated users
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		
		response.getWriter().write("""
				{
					"status": 401,
					"error": "Unauthorized",
					"message": "Authentication is required"
				}
				""");
	}
}
