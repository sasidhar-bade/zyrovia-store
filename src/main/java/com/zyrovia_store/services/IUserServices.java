package com.zyrovia_store.services;

import java.util.List;

import com.zyrovia_store.dtos.UserRequestDto;
import com.zyrovia_store.dtos.UserResponseDto;

public interface IUserServices {

	UserResponseDto registerUser(UserRequestDto requestDto);

	UserResponseDto getUserById(Long userId);

	List<UserResponseDto> getAllUsers();

	UserResponseDto updateUser(Long userId, UserRequestDto requestDto);

	void deleteUser(Long userId);
}
