package com.example.publicapi.controller;

import com.example.publicapi.dto.request.EnrollmentRequestDto;
import com.example.publicapi.dto.response.EnrollmentResponseDto;
import com.example.publicapi.service.EnrollmentService;
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
@RequestMapping("/api/v1/public/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    // Time-bound: rejected with a 400 + descriptive message if outside the
    // course's registration window (see GlobalExceptionHandler).
    @PostMapping
    public ResponseEntity<EnrollmentResponseDto> enroll(@Valid @RequestBody EnrollmentRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentService.enrollStudent(dto));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<Page<EnrollmentResponseDto>> getByStudent(
            @PathVariable UUID studentId,
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudent(studentId, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable UUID id) {
        enrollmentService.cancelEnrollment(id);
        return ResponseEntity.noContent().build();
    }
}
