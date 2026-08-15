package com.zyrovia_store.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

	// Reads JWT secret key from application.properties
	@Value("${jwt.secret}")
	private String jwtSecret;

	// Reads JWT expiration time from application.properties
	@Value("${jwt.expiration}")
	private Long jwtExpiration;

	// Creates signing key used to sign JWT tokens
	// Generate Secret Key from application.properties
	private SecretKey getSignInKey() {

		return Keys.hmacShaKeyFor(
				jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	// Generate JWT Token for authenticated user
	public String generateToken(String email) {

		Date now = new Date();

		Date expiryDate = new Date(now.getTime() + jwtExpiration);

		return Jwts.builder()
				.subject(email)
				.issuedAt(now)
				.expiration(expiryDate)
				.signWith(getSignInKey())
				.compact();
	}

	// Extract email from JWT Token
	public String extractEmail(String token) {

			return extractClaims(token)
					.getSubject();
	}

	// Extract all claims from token
	private Claims extractClaims(String token) {

		return Jwts.parser()
				.verifyWith(this.getSignInKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	// Validate token against user's email and expiration
	public boolean validateToken(String token, String email) {
		
		Claims claims = this.extractClaims(token);

		String extractedEmail = claims.getSubject();
		
		Date expiration= claims.getExpiration();

		return extractedEmail.equals(email) && expiration.after(new Date());
	}
}
