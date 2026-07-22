package com.example.publicapi.controller;

import com.example.core.dto.LoginResponseDto;
import com.example.core.dto.UserLoginDto;
import com.example.core.dto.UserRegisterDto;
import com.example.core.dto.UserResponseDto;
import com.example.publicapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/auth/register")
    public UserResponseDto createUser(@RequestBody @Valid UserRegisterDto userRegisterDto) {
        return userService.createUser(userRegisterDto);
    }

    @PostMapping("/auth/login")
    public LoginResponseDto loginUser(@RequestBody @Valid UserLoginDto userLoginDto) {
        return userService.loginUser(userLoginDto);
    }
}
