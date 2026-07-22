package com.example.admin.service;

import com.example.core.dto.LoginResponseDto;
import com.example.core.dto.UserLoginDto;
import com.example.core.dto.UserRegisterDto;
import com.example.core.dto.UserResponseDto;

public interface UserService {
    UserResponseDto createUser(UserRegisterDto userRegisterDto);
    LoginResponseDto loginUser(UserLoginDto userLoginDto);
}
