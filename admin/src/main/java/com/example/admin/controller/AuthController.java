package com.example.admin.controller;

import com.example.core.dto.LoginResponseDto;
import com.example.core.dto.UserLoginDto;
import com.example.admin.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/auth/login")
    public LoginResponseDto loginUser(@RequestBody @Valid UserLoginDto userLoginDto) {
        return userService.loginUser(userLoginDto);
    }
}
