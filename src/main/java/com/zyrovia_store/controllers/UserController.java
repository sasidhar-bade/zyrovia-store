package com.zyrovia_store.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zyrovia_store.dtos.UserRegistrationRequestDto;
import com.zyrovia_store.dtos.UserResponseDto;
import com.zyrovia_store.dtos.UserUpdateRequestDto;
import com.zyrovia_store.services.IUserServices;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

	// Service layer dependency
	private final IUserServices userServices;

	// Register a new user
	@PostMapping
	public ResponseEntity<UserResponseDto> registerUserApiHandler(@RequestBody UserRegistrationRequestDto registrationRequestDto) {

		UserResponseDto responseDto = this.userServices.registerUser(registrationRequestDto);

		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}

	// Get user by id
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping("/{userId}")
	public ResponseEntity<UserResponseDto> getByUserIdApiHandler(@PathVariable Long userId) {

		return ResponseEntity.ok(this.userServices.getUserById(userId));
	}

	// Get all users
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping
	public ResponseEntity<List<UserResponseDto>> getAllUsersApiHandler() {

		return ResponseEntity.ok(this.userServices.getAllUsers());
	}

	// Update user details
	@PreAuthorize("hasRole('ADMIN')")
	@PatchMapping("/{userId}")
	public ResponseEntity<UserResponseDto> updateUserApiHandler(@PathVariable Long userId,
			@RequestBody UserUpdateRequestDto updateRequestDto) {

		return ResponseEntity.ok(this.userServices.updateUser(userId, updateRequestDto));
	}

	// Delete user by id
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{userId}")
	public ResponseEntity<Void> deleteUserApiHandler(@PathVariable Long userId) {

		this.userServices.deleteUser(userId);

		return ResponseEntity.noContent().build();
	}
}
