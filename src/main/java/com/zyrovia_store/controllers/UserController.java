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

import jakarta.validation.Valid;
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

	// ADMIN can access any user,
    // USER can access only their own profile
	@PreAuthorize("hasRole('ADMIN') or @userSecurity.isOwner(#userId, authentication.name)")
	@GetMapping("/{userId}")
	public ResponseEntity<UserResponseDto> getByUserIdApiHandler(@PathVariable Long userId) {

		return ResponseEntity.ok(this.userServices.getUserById(userId));
	}

	// Only ADMIN can access all users
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping
	public ResponseEntity<List<UserResponseDto>> getAllUsersApiHandler() {

		return ResponseEntity.ok(this.userServices.getAllUsers());
	}

	// ADMIN can update any user
    // USER can update only their own profile
	@PreAuthorize("hasRole('ADMIN') or @userSecurity.isOwner(#userId, authentication.name)")
	@PatchMapping("/{userId}")
	public ResponseEntity<UserResponseDto> updateUserApiHandler(@PathVariable Long userId,
			@Valid @RequestBody UserUpdateRequestDto updateRequestDto) {

		return ResponseEntity.ok(this.userServices.updateUser(userId, updateRequestDto));
	}

	// Only ADMIN can delete users
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{userId}")
	public ResponseEntity<Void> deleteUserApiHandler(@PathVariable Long userId) {

		this.userServices.deleteUser(userId);

		return ResponseEntity.noContent().build();
	}
}
