package com.zyrovia_store.dtos;

import com.zyrovia_store.enums.Role;

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
public class UserRequestDto {

	private String name;

	private String email;

	private String password;

	private Role role;

}
