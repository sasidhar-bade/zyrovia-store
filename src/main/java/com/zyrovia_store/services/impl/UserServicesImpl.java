package com.zyrovia_store.services.impl;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zyrovia_store.dtos.UserRegistrationRequestDto;
import com.zyrovia_store.dtos.UserResponseDto;
import com.zyrovia_store.dtos.UserUpdateRequestDto;
import com.zyrovia_store.entities.User;
import com.zyrovia_store.enums.Role;
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

	// Password encoder used to securely hash user passwords before storing them in
	// the database
	private final BCryptPasswordEncoder passwordEncoder;

	// Convert User Entity to UserResponseDto
	private UserResponseDto mapToResponseDto(User user) {

		// Create response DTO from User entity
		return UserResponseDto.builder()
									.userId(user.getId())
									.name(user.getName())
									.email(user.getEmail())
									.role(user.getRole())
									.build();
	}

	// Register a new user
	@Override
	public UserResponseDto registerUser(UserRegistrationRequestDto userRegistrationRequestDto) {

		// Validate email uniqueness
		if (this.userRepository.existsByEmail(userRegistrationRequestDto.getEmail())) {

			throw new IllegalArgumentException("Email already exists");
		}

		// Create User entity from request DTO
		User user = User.builder()
				.name(userRegistrationRequestDto.getName())
				.email(userRegistrationRequestDto.getEmail())
				.password(passwordEncoder.encode(userRegistrationRequestDto.getPassword()))
				.role(Role.USER)
				.build();

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
	public UserResponseDto updateUser(Long userId, UserUpdateRequestDto userUpdateRequestDto) {

		// Validate user existence
		User user = this.userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		// Update name only if it is provided in the request
		if (userUpdateRequestDto.getName() != null) {

			user.setName(userUpdateRequestDto.getName());
		}

		// Update email only if it is provided
		if (userUpdateRequestDto.getEmail() != null) {

			// Check whether another user is already using this email
			if (!user.getEmail().equals(userUpdateRequestDto.getEmail())
					&& this.userRepository.existsByEmail(userUpdateRequestDto.getEmail())) {

				throw new IllegalArgumentException("Email already exists");
			}

			user.setEmail(userUpdateRequestDto.getEmail());
		}

		// Update password only if provided
		if (userUpdateRequestDto.getPassword() != null 
				&& !userUpdateRequestDto.getPassword().isBlank()) {

			// Encode password before saving into the database
			user.setPassword(passwordEncoder.encode(userUpdateRequestDto.getPassword()));
		}

		// Save updated user
		User updatedUser = this.userRepository.save(user);

		// Convert entity to response DTO
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
