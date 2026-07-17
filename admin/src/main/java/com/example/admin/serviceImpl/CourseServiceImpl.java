package com.example.admin.serviceImpl;


import com.example.admin.dto.request.CourseRequestDto;
import com.example.admin.dto.response.CourseResponseDto;
import com.example.admin.entity.Course;
import com.example.admin.entity.Instructor;
import com.example.admin.exception.FunctionalException;
import com.example.admin.mapper.CourseMapper;
import com.example.admin.repository.CourseRepository;
import com.example.admin.repository.InstructorRepository;
import com.example.admin.service.CourseService;
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
        validateRegistrationWindow(dto);
        Course course = courseMapper.toEntity(dto);
        course.setDeleted(false);
        if (dto.getInstructorId() != null) {
            course.setInstructor(findInstructorOrThrow(dto.getInstructorId()));
        }
        return courseMapper.toResponseDto(courseRepository.save(course));
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponseDto getCourseById(UUID id) {
        return courseMapper.toResponseDto(findActiveCourseOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponseDto> getAllCourses(Pageable pageable) {
        return courseRepository.findAllByDeletedFalse(pageable).map(courseMapper::toResponseDto);
    }

    @Override
    public CourseResponseDto updateCourse(UUID id, CourseRequestDto dto) {
        validateRegistrationWindow(dto);
        Course course = findActiveCourseOrThrow(id);
        courseMapper.updateEntityFromDto(dto, course);
        if (dto.getInstructorId() != null) {
            course.setInstructor(findInstructorOrThrow(dto.getInstructorId()));
        }
        return courseMapper.toResponseDto(courseRepository.save(course));
    }

    @Override
    public void deleteCourse(UUID id) {
        // Soft delete: flip the flag instead of physically removing the row.
        Course course = findActiveCourseOrThrow(id);
        course.setDeleted(true);
        courseRepository.save(course);
    }

    @Override
    public CourseResponseDto assignInstructor(UUID courseId, UUID instructorId) {
        Course course = findActiveCourseOrThrow(courseId);
        Instructor instructor = findInstructorOrThrow(instructorId);
        course.setInstructor(instructor);
        return courseMapper.toResponseDto(courseRepository.save(course));
    }

    private void validateRegistrationWindow(CourseRequestDto dto) {
        if (dto.getRegistrationStartTime() != null && dto.getRegistrationEndTime() != null
                && !dto.getRegistrationStartTime().isBefore(dto.getRegistrationEndTime())) {
            throw new FunctionalException("registrationStartTime must be before registrationEndTime", HttpStatus.BAD_REQUEST);
        }
    }

    private Course findActiveCourseOrThrow(UUID id) {
        return courseRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new FunctionalException("Course not found with id: " + id, HttpStatus.BAD_REQUEST));
    }

    private Instructor findInstructorOrThrow(UUID id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new FunctionalException("Instructor not found with id: " + id, HttpStatus.BAD_REQUEST));
    }
}
