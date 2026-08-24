package com.zyrovia_store.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.zyrovia_store.entities.User;
import com.zyrovia_store.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	// Repository used to fetch user from database
	private final UserRepository userRepository;

	// Load user by email for Spring Security authentication
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = this.userRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		return new CustomUserDetails(user);
	}
}
