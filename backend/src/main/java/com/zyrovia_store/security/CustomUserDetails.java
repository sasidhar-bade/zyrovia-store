package com.zyrovia_store.security;

import java.util.Collection;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.zyrovia_store.entities.User;

public class CustomUserDetails implements UserDetails {

	private static final Long serialVersionUID = 1L;

	// Logged-in user entity
	private final User user;

	public CustomUserDetails(User user) {

		this.user = user;
	}

	// Return user role as GrantedAuthority
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		return List.of(new SimpleGrantedAuthority("ROLE_" + this.user.getRole().name()));
	}

	// Return encrypted password
	@Override
	public @Nullable String getPassword() {

		return this.user.getPassword();
	}

	// Return userName (email)
	@Override
	public String getUsername() {

		return this.user.getEmail();
	}

	// Return actual User entity when needed
	public User getUser() {

		return this.user;
	}
}
