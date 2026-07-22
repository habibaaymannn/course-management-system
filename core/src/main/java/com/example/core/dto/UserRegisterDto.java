package com.example.core.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDto {
    private String name;
    private String email;
    private String password;
}
