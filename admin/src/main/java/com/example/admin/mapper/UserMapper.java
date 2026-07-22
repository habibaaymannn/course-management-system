package com.example.admin.mapper;

import com.example.core.dto.LoginResponseDto;
import com.example.core.dto.UserRegisterDto;
import com.example.core.dto.UserResponseDto;
import com.example.core.entity.Admin;
import com.example.core.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "firstName", source = "name")
    @Mapping(target = "lastName", constant = "")
    @Mapping(target = "role", constant = "ADMIN")
    Admin toUser(UserRegisterDto userRegisterDto);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "name", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
    UserResponseDto toUserResponseDto(User user);

    @Mapping(target = "accessToken", source = "token")
    LoginResponseDto toLoginResponseDto(String token);

}
