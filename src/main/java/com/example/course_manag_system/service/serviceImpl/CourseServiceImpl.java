package com.example.course_manag_system.service.serviceImpl;


import com.example.course_manag_system.dto.request.CourseRequestDto;
import com.example.course_manag_system.dto.response.CourseResponseDto;
import com.example.course_manag_system.entity.Course;
import com.example.course_manag_system.entity.Instructor;
import com.example.course_manag_system.exception.FunctionalException;
import com.example.course_manag_system.mapper.CourseMapper;
import com.example.course_manag_system.repository.CourseRepository;
import com.example.course_manag_system.repository.InstructorRepository;
import com.example.course_manag_system.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final CourseMapper courseMapper;

    @Override
    public CourseResponseDto createCourse(CourseRequestDto dto) {
        Course course = courseMapper.toCourse(dto);
        if (dto.getInstructorId() != null) {
            course.setInstructor(findInstructor(dto.getInstructorId()));
        }
        return courseMapper.toResponseDto(courseRepository.save(course));
    }

    @Override
    public CourseResponseDto getCourseById(UUID id) {
        return courseMapper.toResponseDto(findActiveCourse(id));
    }

    @Override
    public Page<CourseResponseDto> getAllCourses(Pageable pageable) {
        return courseRepository.findAllByDeletedFalse(pageable).map(courseMapper::toResponseDto);
    }

    @Override
    public CourseResponseDto updateCourse(UUID id, CourseRequestDto dto) {
        Course course = findActiveCourse(id);
        courseMapper.updateEntityFromDto(dto, course);
        if (dto.getInstructorId() != null) {
            course.setInstructor(findInstructor(dto.getInstructorId()));
        }
        return courseMapper.toResponseDto(courseRepository.save(course));
    }

    @Override
    public void deleteCourse(UUID id) {
        // Soft delete: flip the flag instead of physically removing the row,
        // so historical enrollment data referencing this course stays intact.
        Course course = findActiveCourse(id);
        course.setDeleted(true);
        courseRepository.save(course);
    }

    @Override
    public CourseResponseDto assignInstructor(UUID courseId, UUID instructorId) {
        Course course = findActiveCourse(courseId);
        Instructor instructor = findInstructor(instructorId);
        course.setInstructor(instructor);
        return courseMapper.toResponseDto(courseRepository.save(course));
    }

    private Course findActiveCourse(UUID id) {
        return courseRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new FunctionalException("Course not found with id: " + id, HttpStatus.BAD_REQUEST));
    }

    private Instructor findInstructor(UUID id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new FunctionalException("Instructor not found with id: " + id, HttpStatus.BAD_REQUEST));
    }
}
