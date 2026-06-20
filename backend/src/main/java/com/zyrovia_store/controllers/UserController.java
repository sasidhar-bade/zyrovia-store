package com.zyrovia_store.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zyrovia_store.dtos.UserRequestDto;
import com.zyrovia_store.dtos.UserResponseDto;
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
	public ResponseEntity<UserResponseDto> registerUserApiHandler(@RequestBody UserRequestDto requestDto) {

		UserResponseDto responseDto = this.userServices.registerUser(requestDto);

		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}

	// Get user by id
	@GetMapping("/{userId}")
	public ResponseEntity<UserResponseDto> getByUserIdApiHandler(@PathVariable Long userId) {

		return ResponseEntity.ok(this.userServices.getUserById(userId));
	}

	// Get all users
	@GetMapping
	public ResponseEntity<List<UserResponseDto>> getAllUsersApiHandler() {

		return ResponseEntity.ok(this.userServices.getAllUsers());
	}

	// Update user details
	@PatchMapping("/{userId}")
	public ResponseEntity<UserResponseDto> updateUserApiHandler(@PathVariable Long userId,
			@RequestBody UserRequestDto requestDto) {

		return ResponseEntity.ok(this.userServices.updateUser(userId, requestDto));
	}

	// Delete user by id
	@DeleteMapping("/{userId}")
	public ResponseEntity<Void> deleteUserApiHandler(@PathVariable Long userId) {

		this.userServices.deleteUser(userId);

		return ResponseEntity.noContent().build();
	}
}
