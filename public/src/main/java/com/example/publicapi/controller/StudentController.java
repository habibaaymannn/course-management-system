package com.example.publicapi.controller;

import com.example.publicapi.dto.request.StudentRequestDto;
import com.example.publicapi.dto.response.StudentResponseDto;
import com.example.publicapi.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/me")
    public ResponseEntity<StudentResponseDto> getMe() {
        return ResponseEntity.ok(studentService.getCurrentStudent());
    }
}
