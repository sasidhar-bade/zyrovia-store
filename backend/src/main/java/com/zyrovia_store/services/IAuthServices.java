package com.zyrovia_store.services;

import com.zyrovia_store.dtos.LoginRequestDto;
import com.zyrovia_store.dtos.LoginResponseDto;

public interface IAuthServices {

	LoginResponseDto login(LoginRequestDto requestDto);

}
