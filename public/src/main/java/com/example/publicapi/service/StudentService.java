package com.example.publicapi.service;

import com.example.publicapi.dto.request.StudentRequestDto;
import com.example.publicapi.dto.response.StudentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface StudentService {
    StudentResponseDto createStudent(StudentRequestDto dto);
    StudentResponseDto getStudentById(UUID id);
}
