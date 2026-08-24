package com.zyrovia_store.services.impl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.zyrovia_store.dtos.LoginRequestDto;
import com.zyrovia_store.dtos.LoginResponseDto;
import com.zyrovia_store.entities.User;
import com.zyrovia_store.exceptions.ResourceNotFoundException;
import com.zyrovia_store.repositories.UserRepository;
import com.zyrovia_store.security.JwtUtils;
import com.zyrovia_store.services.IAuthServices;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServicesImpl implements IAuthServices {

	// Repository for user database operations
	private final UserRepository userRepository;

	// Used to verify the raw password with the encrypted password stored in the
	// database
	private final BCryptPasswordEncoder passwordEncoder;

	// Utility class used for generating JWT tokens
	private final JwtUtils jwtUtils;

	// Authenticate user using email and password
	@Override
	public LoginResponseDto login(LoginRequestDto requestDto) {

		// Find user by email
		User user = this.userRepository
				.findByEmail(requestDto.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException("Invalid Email or Password"));

		// Verify the entered password with the encrypted password
		if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {

			throw new IllegalArgumentException("Invalid Email or Password");
		}

		// Generate JWT token after successful authentication
		String token = this.jwtUtils.generateToken(user.getEmail());

		// Return JWT token to the client
		return LoginResponseDto.builder()
				.token(token)
				.type("Bearer")
				.build();
	}
}
