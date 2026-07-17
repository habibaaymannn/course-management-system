package com.example.admin.service;


import com.example.admin.dto.request.CourseRequestDto;
import com.example.admin.dto.response.CourseResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CourseService {
    CourseResponseDto createCourse(CourseRequestDto dto);
    CourseResponseDto getCourseById(UUID id);
    Page<CourseResponseDto> getAllCourses(Pageable pageable);
    CourseResponseDto updateCourse(UUID id, CourseRequestDto dto);
    void deleteCourse(UUID id);
    CourseResponseDto assignInstructor(UUID courseId, UUID instructorId);
}
