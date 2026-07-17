package com.example.publicapi.service;

import com.example.publicapi.dto.response.CourseResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

// Read-only, student-facing course browsing.
public interface CourseService {
    CourseResponseDto getCourseById(UUID id);
    Page<CourseResponseDto> getAllCourses(Pageable pageable);
}
