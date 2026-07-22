package com.example.publicapi.serviceImplTest;

import com.example.publicapi.dto.response.CourseResponseDto;
import com.example.core.entity.Course;
import com.example.core.exception.FunctionalException;
import com.example.publicapi.mapper.CourseMapper;
import com.example.publicapi.repository.CourseRepository;
import com.example.publicapi.serviceImpl.CourseServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock private CourseRepository courseRepository;
    @Mock private CourseMapper courseMapper;
    @InjectMocks private CourseServiceImpl courseService;

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
}
