package com.example.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserResponseDto {
    UUID userId;
    String name;
    String email;

}
