package com.zyrovia_store.security;

import org.springframework.stereotype.Component;

import com.zyrovia_store.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserSecurity {
	
	private final UserRepository userRepository;
	
    // Check whether the requested user belongs to the logged-in user
	public boolean isOwner(Long userID, String email) {
		
		return this.userRepository.findById(userID)
				.map(user -> user.getEmail().equals(email))
				.orElse(false);
	}
}
