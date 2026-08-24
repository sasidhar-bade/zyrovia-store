package com.zyrovia_store.services;

import java.util.List;

import com.zyrovia_store.dtos.UserRegistrationRequestDto;
import com.zyrovia_store.dtos.UserResponseDto;
import com.zyrovia_store.dtos.UserRoleUpdateRequestDto;
import com.zyrovia_store.dtos.UserUpdateRequestDto;

public interface IUserServices {

	UserResponseDto registerUser(UserRegistrationRequestDto requestDto);

	UserResponseDto getUserById(Long userId);

	List<UserResponseDto> getAllUsers();

	UserResponseDto updateUser(Long userId, UserUpdateRequestDto requestDto);

	void deleteUser(Long userId);
	
	UserResponseDto updateUserRole(Long userId, UserRoleUpdateRequestDto roleUpdateRequestDto);
}