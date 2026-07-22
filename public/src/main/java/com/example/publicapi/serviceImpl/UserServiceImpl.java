package com.example.publicapi.serviceImpl;

import com.example.core.repository.UserRepository;
import com.example.publicapi.service.UserService;
import org.springframework.http.HttpStatus;
import com.example.core.auth.JwtService;
import com.example.core.dto.LoginResponseDto;
import com.example.core.dto.UserLoginDto;
import com.example.core.dto.UserRegisterDto;
import com.example.core.dto.UserResponseDto;
import com.example.core.entity.User;
import com.example.core.exception.FunctionalException;
import com.example.publicapi.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto createUser(UserRegisterDto userRegisterDto){
        validateUserInfo(userRegisterDto);
        if (userRepository.existsByEmail((userRegisterDto.getEmail()))) {
            throw new FunctionalException("Email already exists", HttpStatus.BAD_REQUEST);
        }
        User user = userMapper.toUser(userRegisterDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toUserResponseDto(savedUser);
    }

    public LoginResponseDto loginUser(UserLoginDto userLoginDto){
        User user = userRepository.findByEmail(userLoginDto.getEmail())
                .orElseThrow(() -> new FunctionalException("Invalid credentials", HttpStatus.BAD_REQUEST));
        if (!passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword())) {
            throw new FunctionalException("Invalid credentials", HttpStatus.BAD_REQUEST);
        }
        String token = jwtService.generateToken(user);
        return userMapper.toLoginResponseDto(token);
    }

    private void validateUserInfo(UserRegisterDto dto) {

        if (dto.getName() == null || dto.getName().isEmpty()) {
            throw new FunctionalException("Please provide a name", HttpStatus.BAD_REQUEST);
        }

        if (dto.getEmail() == null || !dto.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new FunctionalException("Invalid email format", HttpStatus.BAD_REQUEST);
        }

        if (dto.getPassword() == null || dto.getPassword().length() < 8) {
            throw new FunctionalException("Password must be at least 8 characters", HttpStatus.BAD_REQUEST);
        }
    }
}
