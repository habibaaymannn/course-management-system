package com.example.course_manag_system.controller;


import com.example.course_manag_system.dto.request.EnrollmentRequestDto;
import com.example.course_manag_system.dto.response.EnrollmentResponseDto;
import com.example.course_manag_system.entity.EnrollmentStatus;
import com.example.course_manag_system.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentResponseDto> enroll(@Valid @RequestBody EnrollmentRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentService.enrollStudent(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<Page<EnrollmentResponseDto>> getByStudent(@PathVariable UUID studentId, @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudent(studentId, pageable));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<Page<EnrollmentResponseDto>> getByCourse(@PathVariable UUID courseId, @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourse(courseId, pageable));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<EnrollmentResponseDto> updateStatus(@PathVariable UUID id, @RequestParam EnrollmentStatus status) {
        return ResponseEntity.ok(enrollmentService.updateEnrollmentStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable UUID id) {
        enrollmentService.cancelEnrollment(id);
        return ResponseEntity.noContent().build();
    }
}
