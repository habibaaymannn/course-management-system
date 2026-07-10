package com.example.course_manag_system.controller;


import com.example.course_manag_system.dto.request.CourseRequestDto;
import com.example.course_manag_system.dto.response.CourseResponseDto;
import com.example.course_manag_system.service.CourseService;
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
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseResponseDto> create(@Valid @RequestBody CourseRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    // Pagination + sorting, e.g. GET /api/v1/courses?page=0&size=10&sort=title,asc
    @GetMapping
    public ResponseEntity<Page<CourseResponseDto>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(courseService.getAllCourses(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDto> update(@PathVariable UUID id, @Valid @RequestBody CourseRequestDto dto) {
        return ResponseEntity.ok(courseService.updateCourse(id, dto));
    }

    @PatchMapping("/{courseId}/instructor/{instructorId}")
    public ResponseEntity<CourseResponseDto> assignInstructor(@PathVariable UUID courseId, @PathVariable UUID instructorId) {
        return ResponseEntity.ok(courseService.assignInstructor(courseId, instructorId));
    }

    // Soft delete - course row stays in the DB but is excluded from future reads.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
