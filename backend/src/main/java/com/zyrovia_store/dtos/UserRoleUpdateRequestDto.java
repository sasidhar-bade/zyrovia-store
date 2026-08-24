package com.zyrovia_store.dtos;

import com.zyrovia_store.enums.Role;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleUpdateRequestDto {

	@NotNull(message = "Role cannot be null")
	private Role role;
}