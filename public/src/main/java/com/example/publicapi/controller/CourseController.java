package com.example.publicapi.controller;

import com.example.publicapi.dto.response.CourseResponseDto;
import com.example.publicapi.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/public/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping
    public ResponseEntity<Page<CourseResponseDto>> getAll(
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(courseService.getAllCourses(pageable));
    }
}
