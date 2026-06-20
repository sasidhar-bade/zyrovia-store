package com.zyrovia_store.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zyrovia_store.dtos.UserRequestDto;
import com.zyrovia_store.dtos.UserResponseDto;
import com.zyrovia_store.entities.User;
import com.zyrovia_store.exceptions.ResourceNotFoundException;
import com.zyrovia_store.repositories.UserRepository;
import com.zyrovia_store.services.IUserServices;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServicesImpl implements IUserServices {

	// Repository for User database operations
	private final UserRepository userRepository;

	// Convert User Entity to UserResponseDto
	private UserResponseDto mapToResponseDto(User user) {

		// Create response DTO from User entity
		return UserResponseDto.builder().userId(user.getId()).name(user.getName()).email(user.getEmail())
				.role(user.getRole()).build();
	}

	// Register a new user
	@Override
	public UserResponseDto registerUser(UserRequestDto requestDto) {

		// Validate email uniqueness
		if (this.userRepository.existsByEmail(requestDto.getEmail())) {

			throw new IllegalArgumentException("Email already exists");
		}

		// Create User entity from request DTO
		User user = User.builder().name(requestDto.getName()).email(requestDto.getEmail())
				.password(requestDto.getPassword()).role(requestDto.getRole()).build();

		User savedUser = this.userRepository.save(user);

		return this.mapToResponseDto(savedUser);
	}

	// Fetch user by id
	@Override
	public UserResponseDto getUserById(Long userId) {

		// Validate user existence
		User user = this.userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		return this.mapToResponseDto(user);
	}

	// Fetch all users
	@Override
	public List<UserResponseDto> getAllUsers() {

		return this.userRepository.findAll().stream().map(this::mapToResponseDto).toList();
	}

	// Update existing user details
	@Override
	public UserResponseDto updateUser(Long userId, UserRequestDto requestDto) {

		// Validate user existence
		User user = this.userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		user.setName(requestDto.getName());

		// Validate email uniqueness during update
		if (!user.getEmail().equals(requestDto.getEmail())
				&& this.userRepository.existsByEmail(requestDto.getEmail())) {

			throw new IllegalArgumentException("Email already exists");
		}

		user.setEmail(requestDto.getEmail());

		// Update password only if provided
		if (requestDto.getPassword() != null && requestDto.getEmail().isBlank()) {
			user.setPassword(requestDto.getPassword());
		}

		user.setRole(requestDto.getRole());

		User updatedUser = this.userRepository.save(user);

		return this.mapToResponseDto(updatedUser);
	}

	// Delete user by id
	@Override
	public void deleteUser(Long userId) {

		// Validate user existence
		User user = this.userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		this.userRepository.delete(user);
	}

}
