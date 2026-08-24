package com.zyrovia_store.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zyrovia_store.dtos.LoginRequestDto;
import com.zyrovia_store.dtos.LoginResponseDto;
import com.zyrovia_store.services.IAuthServices;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth") // Base URL for authentication APIs
@RequiredArgsConstructor
public class AuthController {

	// Service layer dependency for authentication operations
	private final IAuthServices authServices;

	// Login API
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> loginApiHandler(@Valid @RequestBody LoginRequestDto requestDto) {

		// Authenticate the user and return JWT token response
		return ResponseEntity.ok(this.authServices.login(requestDto));
	}

}
