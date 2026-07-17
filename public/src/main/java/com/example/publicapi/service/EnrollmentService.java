package com.example.publicapi.service;

import com.example.publicapi.dto.request.EnrollmentRequestDto;
import com.example.publicapi.dto.response.EnrollmentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface EnrollmentService {
    EnrollmentResponseDto enrollStudent(EnrollmentRequestDto dto);
    Page<EnrollmentResponseDto> getEnrollmentsByStudent(UUID studentId, Pageable pageable);
    void cancelEnrollment(UUID id);
}
