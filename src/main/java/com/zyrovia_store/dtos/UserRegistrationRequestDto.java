package com.zyrovia_store.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationRequestDto {

	@NotNull(message = "Name is required")
	private String name;

	@NotNull(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotNull(message = "Password is required")
	@Size(min = 8, message = "Password must contain at least 8 characters")
	private String password;
}