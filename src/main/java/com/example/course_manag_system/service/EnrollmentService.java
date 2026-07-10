package com.example.course_manag_system.service;


import com.example.course_manag_system.dto.request.EnrollmentRequestDto;
import com.example.course_manag_system.dto.response.EnrollmentResponseDto;
import com.example.course_manag_system.entity.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface EnrollmentService {
    EnrollmentResponseDto enrollStudent(EnrollmentRequestDto dto);
    EnrollmentResponseDto getEnrollmentById(UUID id);
    Page<EnrollmentResponseDto> getEnrollmentsByStudent(UUID studentId, Pageable pageable);
    Page<EnrollmentResponseDto> getEnrollmentsByCourse(UUID courseId, Pageable pageable);
    EnrollmentResponseDto updateEnrollmentStatus(UUID id, EnrollmentStatus status);
    void cancelEnrollment(UUID id);
}
