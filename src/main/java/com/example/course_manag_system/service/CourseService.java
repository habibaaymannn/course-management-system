package com.example.course_manag_system.service;


import com.example.course_manag_system.dto.request.CourseRequestDto;
import com.example.course_manag_system.dto.response.CourseResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CourseService {
    CourseResponseDto createCourse(CourseRequestDto dto);
    CourseResponseDto getCourseById(UUID id);
    Page<CourseResponseDto> getAllCourses(Pageable pageable);
    CourseResponseDto updateCourse(UUID id, CourseRequestDto dto);
    // Soft delete: marks the course as deleted rather than removing the row.
    void deleteCourse(UUID id);
    CourseResponseDto assignInstructor(UUID courseId, UUID instructorId);
}
