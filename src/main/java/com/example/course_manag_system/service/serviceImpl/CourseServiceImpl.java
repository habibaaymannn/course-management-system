package com.coursemanagement.serviceImpl;

import com.coursemanagement.dto.request.CourseRequestDto;
import com.coursemanagement.dto.response.CourseResponseDto;
import com.coursemanagement.entity.Course;
import com.coursemanagement.entity.Instructor;
import com.coursemanagement.exception.ResourceNotFoundException;
import com.coursemanagement.repository.CourseRepository;
import com.coursemanagement.repository.InstructorRepository;
import com.coursemanagement.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;

    @Override
    public CourseResponseDto createCourse(CourseRequestDto dto) {
        Course course = Course.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .credits(dto.getCredits())
                .deleted(false)
                .build();

        if (dto.getInstructorId() != null) {
            course.setInstructor(findInstructorOrThrow(dto.getInstructorId()));
        }

        return toResponseDto(courseRepository.save(course));
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponseDto getCourseById(Long id) {
        return toResponseDto(findActiveCourseOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponseDto> getAllCourses(Pageable pageable) {
        return courseRepository.findAllByDeletedFalse(pageable).map(this::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponseDto> searchCoursesByTitle(String keyword, Pageable pageable) {
        return courseRepository.searchByTitle(keyword, pageable).map(this::toResponseDto);
    }

    @Override
    public CourseResponseDto updateCourse(Long id, CourseRequestDto dto) {
        Course course = findActiveCourseOrThrow(id);

        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setCredits(dto.getCredits());

        if (dto.getInstructorId() != null) {
            course.setInstructor(findInstructorOrThrow(dto.getInstructorId()));
        }

        return toResponseDto(courseRepository.save(course));
    }

    @Override
    public void deleteCourse(Long id) {
        // Soft delete: flip the flag instead of physically removing the row,
        // so historical enrollment data referencing this course stays intact.
        Course course = findActiveCourseOrThrow(id);
        course.setDeleted(true);
        courseRepository.save(course);
    }

    @Override
    public CourseResponseDto assignInstructor(Long courseId, Long instructorId) {
        Course course = findActiveCourseOrThrow(courseId);
        Instructor instructor = findInstructorOrThrow(instructorId);
        course.setInstructor(instructor);
        return toResponseDto(courseRepository.save(course));
    }

    private Course findActiveCourseOrThrow(Long id) {
        return courseRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    private Instructor findInstructorOrThrow(Long id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + id));
    }

    private CourseResponseDto toResponseDto(Course course) {
        int enrolledCount = course.getEnrollments() == null ? 0 : course.getEnrollments().size();
        return CourseResponseDto.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .credits(course.getCredits())
                .instructorId(course.getInstructor() != null ? course.getInstructor().getId() : null)
                .instructorName(course.getInstructor() != null
                        ? course.getInstructor().getFirstName() + " " + course.getInstructor().getLastName()
                        : null)
                .enrolledStudentCount(enrolledCount)
                .build();
    }
}
