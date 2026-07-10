package com.coursemanagement.controller;

import com.coursemanagement.dto.request.CourseRequestDto;
import com.coursemanagement.dto.response.CourseResponseDto;
import com.coursemanagement.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<CourseResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    // Pagination + sorting, e.g. GET /api/v1/courses?page=0&size=10&sort=title,asc
    @GetMapping
    public ResponseEntity<Page<CourseResponseDto>> getAll(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(courseService.getAllCourses(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CourseResponseDto>> search(
            @RequestParam String title,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(courseService.searchCoursesByTitle(title, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDto> update(@PathVariable Long id,
                                                      @Valid @RequestBody CourseRequestDto dto) {
        return ResponseEntity.ok(courseService.updateCourse(id, dto));
    }

    @PatchMapping("/{courseId}/instructor/{instructorId}")
    public ResponseEntity<CourseResponseDto> assignInstructor(@PathVariable Long courseId,
                                                                @PathVariable Long instructorId) {
        return ResponseEntity.ok(courseService.assignInstructor(courseId, instructorId));
    }

    // Soft delete - course row stays in the DB but is excluded from future reads.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
