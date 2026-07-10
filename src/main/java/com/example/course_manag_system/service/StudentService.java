package com.example.course_manag_system.service;


import com.example.course_manag_system.dto.request.StudentRequestDto;
import com.example.course_manag_system.dto.response.StudentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface StudentService {
    StudentResponseDto createStudent(StudentRequestDto dto);
    StudentResponseDto getStudentById(UUID id);
    Page<StudentResponseDto> getAllStudents(Pageable pageable);
    StudentResponseDto updateStudent(UUID id, StudentRequestDto dto);
    void deleteStudent(UUID id);
}
