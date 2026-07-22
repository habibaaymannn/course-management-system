package com.example.admin.serviceImplTest;

import com.example.admin.dto.request.CourseRequestDto;
import com.example.admin.dto.response.CourseResponseDto;
import com.example.core.entity.Course;
import com.example.core.entity.Instructor;
import com.example.core.exception.FunctionalException;
import com.example.admin.mapper.CourseMapper;
import com.example.admin.repository.CourseRepository;
import com.example.admin.repository.InstructorRepository;
import com.example.admin.serviceImpl.CourseServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock private CourseRepository courseRepository;
    @Mock private InstructorRepository instructorRepository;
    @Mock private CourseMapper courseMapper;
    @InjectMocks private CourseServiceImpl courseService;

    @Test
    void createCourseSavesCourseWithInstructorWhenInstructorIdIsProvided() {
        UUID instructorId = UUID.randomUUID();
        CourseRequestDto dto = courseRequest(instructorId);
        Course mappedCourse = new Course();
        Instructor instructor = Instructor.builder().id(instructorId).build();
        Course savedCourse = Course.builder().id(UUID.randomUUID()).instructor(instructor).build();
        CourseResponseDto response = CourseResponseDto.builder().id(savedCourse.getId()).instructorId(instructorId).build();

        when(courseMapper.toEntity(dto)).thenReturn(mappedCourse);
        when(instructorRepository.findById(instructorId)).thenReturn(Optional.of(instructor));
        when(courseRepository.save(mappedCourse)).thenReturn(savedCourse);
        when(courseMapper.toResponseDto(savedCourse)).thenReturn(response);

        CourseResponseDto result = courseService.createCourse(dto);

        assertThat(result).isSameAs(response);
        assertThat(mappedCourse.isDeleted()).isFalse();
        assertThat(mappedCourse.getInstructor()).isSameAs(instructor);
        verify(courseRepository).save(mappedCourse);
    }

    @Test
    void createCourseThrowsWhenRegistrationStartIsNotBeforeEnd() {
        CourseRequestDto dto = CourseRequestDto.builder()
                .title("Java")
                .credits(3)
                .registrationStartTime(LocalDateTime.parse("2026-01-02T10:00:00"))
                .registrationEndTime(LocalDateTime.parse("2026-01-02T10:00:00"))
                .build();

        assertThatThrownBy(() -> courseService.createCourse(dto))
                .isInstanceOf(FunctionalException.class)
                .hasMessage("registrationStartTime must be before registrationEndTime")
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(courseRepository, never()).save(any());
    }

    @Test
    void getCourseByIdReturnsMappedActiveCourse() {
        UUID courseId = UUID.randomUUID();
        Course course = Course.builder().id(courseId).title("Java").build();
        CourseResponseDto response = CourseResponseDto.builder().id(courseId).title("Java").build();

        when(courseRepository.findByIdAndDeletedFalse(courseId)).thenReturn(Optional.of(course));
        when(courseMapper.toResponseDto(course)).thenReturn(response);

        CourseResponseDto result = courseService.getCourseById(courseId);

        assertThat(result).isSameAs(response);
    }

    @Test
    void getCourseByIdThrowsWhenCourseDoesNotExist() {
        UUID courseId = UUID.randomUUID();
        when(courseRepository.findByIdAndDeletedFalse(courseId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.getCourseById(courseId))
                .isInstanceOf(FunctionalException.class)
                .hasMessage("Course not found with id: " + courseId)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getAllCoursesReturnsMappedPage() {
        PageRequest pageable = PageRequest.of(0, 10);
        Course course = Course.builder().id(UUID.randomUUID()).title("Java").build();
        CourseResponseDto response = CourseResponseDto.builder().id(course.getId()).title("Java").build();

        when(courseRepository.findAllByDeletedFalse(pageable)).thenReturn(new PageImpl<>(List.of(course), pageable, 1));
        when(courseMapper.toResponseDto(course)).thenReturn(response);

        Page<CourseResponseDto> result = courseService.getAllCourses(pageable);

        assertThat(result.getContent()).containsExactly(response);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void updateCourseUpdatesExistingCourseAndAssignsInstructor() {
        UUID courseId = UUID.randomUUID();
        UUID instructorId = UUID.randomUUID();
        CourseRequestDto dto = courseRequest(instructorId);
        Course existingCourse = Course.builder().id(courseId).title("Old").build();
        Instructor instructor = Instructor.builder().id(instructorId).build();
        Course savedCourse = Course.builder().id(courseId).title("New").instructor(instructor).build();
        CourseResponseDto response = CourseResponseDto.builder().id(courseId).title("New").instructorId(instructorId).build();

        when(courseRepository.findByIdAndDeletedFalse(courseId)).thenReturn(Optional.of(existingCourse));
        when(instructorRepository.findById(instructorId)).thenReturn(Optional.of(instructor));
        when(courseRepository.save(existingCourse)).thenReturn(savedCourse);
        when(courseMapper.toResponseDto(savedCourse)).thenReturn(response);

        CourseResponseDto result = courseService.updateCourse(courseId, dto);

        assertThat(result).isSameAs(response);
        assertThat(existingCourse.getInstructor()).isSameAs(instructor);
        verify(courseMapper).updateEntityFromDto(dto, existingCourse);
        verify(courseRepository).save(existingCourse);
    }

    @Test
    void deleteCourseMarksCourseDeleted() {
        UUID courseId = UUID.randomUUID();
        Course course = Course.builder().id(courseId).deleted(false).build();
        when(courseRepository.findByIdAndDeletedFalse(courseId)).thenReturn(Optional.of(course));

        courseService.deleteCourse(courseId);

        assertThat(course.isDeleted()).isTrue();
        verify(courseRepository).save(course);
    }

    @Test
    void assignInstructorSavesCourseWithInstructor() {
        UUID courseId = UUID.randomUUID();
        UUID instructorId = UUID.randomUUID();
        Course course = Course.builder().id(courseId).build();
        Instructor instructor = Instructor.builder().id(instructorId).build();
        Course savedCourse = Course.builder().id(courseId).instructor(instructor).build();
        CourseResponseDto response = CourseResponseDto.builder().id(courseId).instructorId(instructorId).build();

        when(courseRepository.findByIdAndDeletedFalse(courseId)).thenReturn(Optional.of(course));
        when(instructorRepository.findById(instructorId)).thenReturn(Optional.of(instructor));
        when(courseRepository.save(course)).thenReturn(savedCourse);
        when(courseMapper.toResponseDto(savedCourse)).thenReturn(response);

        CourseResponseDto result = courseService.assignInstructor(courseId, instructorId);

        assertThat(result).isSameAs(response);
        assertThat(course.getInstructor()).isSameAs(instructor);
    }

    @Test
    void assignInstructorThrowsWhenInstructorDoesNotExist() {
        UUID courseId = UUID.randomUUID();
        UUID instructorId = UUID.randomUUID();
        Course course = Course.builder().id(courseId).build();

        when(courseRepository.findByIdAndDeletedFalse(courseId)).thenReturn(Optional.of(course));
        when(instructorRepository.findById(instructorId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.assignInstructor(courseId, instructorId))
                .isInstanceOf(FunctionalException.class)
                .hasMessage("Instructor not found with id: " + instructorId)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(courseRepository, never()).save(any());
    }

    private CourseRequestDto courseRequest(UUID instructorId) {
        return CourseRequestDto.builder()
                .title("Java")
                .description("Intro")
                .credits(3)
                .instructorId(instructorId)
                .registrationStartTime(LocalDateTime.parse("2026-01-01T10:00:00"))
                .registrationEndTime(LocalDateTime.parse("2026-01-02T10:00:00"))
                .build();
    }
}
