package com.zyrovia_store.dtos;

import jakarta.validation.constraints.Email;
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
public class UserUpdateRequestDto {

	@Size(
			min = 2, 
			message = "Name must contain at least 2 characters"
			)
	private String name;

	@Email(message = "Invaild email format")
	private String email;

	@Size(
			min = 8, 
			message = "Password must contain at least 8 characters"
			)
	private String password;
}