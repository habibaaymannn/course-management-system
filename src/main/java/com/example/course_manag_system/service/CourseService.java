package com.coursemanagement.service;

import com.coursemanagement.dto.request.CourseRequestDto;
import com.coursemanagement.dto.response.CourseResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseService {
    CourseResponseDto createCourse(CourseRequestDto dto);
    CourseResponseDto getCourseById(Long id);
    Page<CourseResponseDto> getAllCourses(Pageable pageable);
    Page<CourseResponseDto> searchCoursesByTitle(String keyword, Pageable pageable);
    CourseResponseDto updateCourse(Long id, CourseRequestDto dto);

    // Soft delete: marks the course as deleted rather than removing the row.
    void deleteCourse(Long id);

    CourseResponseDto assignInstructor(Long courseId, Long instructorId);
}
