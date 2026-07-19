package com.example.publicapi.serviceImplTest;

import com.example.publicapi.dto.request.EnrollmentRequestDto;
import com.example.publicapi.dto.response.EnrollmentResponseDto;
import com.example.publicapi.entity.Course;
import com.example.publicapi.entity.Enrollment;
import com.example.publicapi.entity.EnrollmentStatus;
import com.example.publicapi.entity.Student;
import com.example.publicapi.exception.FunctionalException;
import com.example.publicapi.mapper.EnrollmentMapper;
import com.example.publicapi.repository.CourseRepository;
import com.example.publicapi.repository.EnrollmentRepository;
import com.example.publicapi.repository.StudentRepository;
import com.example.publicapi.serviceImpl.EnrollmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class EnrollmentServiceImplTest {

    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private CourseRepository courseRepository;
    @Mock private EnrollmentMapper enrollmentMapper;
    @InjectMocks private EnrollmentServiceImpl enrollmentService;

    @Test
    void enrollStudentSavesEnrollmentWhenStudentCourseAndRegistrationWindowAreValid() {
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        EnrollmentRequestDto dto = enrollmentRequest(studentId, courseId);
        Student student = Student.builder().id(studentId).firstName("Ada").lastName("Lovelace").build();
        Course course = openCourse(courseId);
        Enrollment savedEnrollment = Enrollment.builder().id(UUID.randomUUID()).student(student).course(course).build();
        EnrollmentResponseDto response = EnrollmentResponseDto.builder()
                .id(savedEnrollment.getId())
                .studentId(studentId)
                .courseId(courseId)
                .status(EnrollmentStatus.ACTIVE)
                .build();

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(courseRepository.findByIdAndDeletedFalse(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)).thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(savedEnrollment);
        when(enrollmentMapper.toResponseDto(savedEnrollment)).thenReturn(response);

        EnrollmentResponseDto result = enrollmentService.enrollStudent(dto);

        assertThat(result).isSameAs(response);
        ArgumentCaptor<Enrollment> enrollmentCaptor = ArgumentCaptor.forClass(Enrollment.class);
        verify(enrollmentRepository).save(enrollmentCaptor.capture());
        assertThat(enrollmentCaptor.getValue().getStudent()).isSameAs(student);
        assertThat(enrollmentCaptor.getValue().getCourse()).isSameAs(course);
        assertThat(enrollmentCaptor.getValue().getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
    }

    @Test
    void enrollStudentThrowsWhenStudentDoesNotExist() {
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        EnrollmentRequestDto dto = enrollmentRequest(studentId, courseId);

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.enrollStudent(dto))
                .isInstanceOf(FunctionalException.class)
                .hasMessage("Student not found with id: " + studentId)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void enrollStudentThrowsWhenCourseDoesNotExist() {
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        EnrollmentRequestDto dto = enrollmentRequest(studentId, courseId);
        Student student = Student.builder().id(studentId).build();

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(courseRepository.findByIdAndDeletedFalse(courseId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.enrollStudent(dto))
                .isInstanceOf(FunctionalException.class)
                .hasMessage("Course not found with id: " + courseId)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void enrollStudentThrowsWhenStudentIsAlreadyEnrolled() {
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        EnrollmentRequestDto dto = enrollmentRequest(studentId, courseId);
        Student student = Student.builder().id(studentId).build();
        Course course = openCourse(courseId);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(courseRepository.findByIdAndDeletedFalse(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)).thenReturn(true);

        assertThatThrownBy(() -> enrollmentService.enrollStudent(dto))
                .isInstanceOf(FunctionalException.class)
                .hasMessage("Student is already enrolled in this course")
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void enrollStudentThrowsWhenRegistrationWindowIsNotConfigured() {
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        EnrollmentRequestDto dto = enrollmentRequest(studentId, courseId);
        Student student = Student.builder().id(studentId).build();
        Course course = Course.builder().id(courseId).title("Java").build();

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(courseRepository.findByIdAndDeletedFalse(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)).thenReturn(false);

        assertThatThrownBy(() -> enrollmentService.enrollStudent(dto))
                .isInstanceOf(FunctionalException.class)
                .hasMessage("Registration for course 'Java' has not been configured yet")
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void enrollStudentThrowsWhenRegistrationHasNotOpened() {
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        EnrollmentRequestDto dto = enrollmentRequest(studentId, courseId);
        Student student = Student.builder().id(studentId).build();
        Course course = Course.builder()
                .id(courseId)
                .title("Java")
                .registrationStartTime(LocalDateTime.now().plusDays(1))
                .registrationEndTime(LocalDateTime.now().plusDays(2))
                .build();

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(courseRepository.findByIdAndDeletedFalse(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)).thenReturn(false);

        assertThatThrownBy(() -> enrollmentService.enrollStudent(dto))
                .isInstanceOf(FunctionalException.class)
                .hasMessageContaining("Registration for course 'Java' has not opened yet. It opens on")
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void enrollStudentThrowsWhenRegistrationHasClosed() {
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        EnrollmentRequestDto dto = enrollmentRequest(studentId, courseId);
        Student student = Student.builder().id(studentId).build();
        Course course = Course.builder()
                .id(courseId)
                .title("Java")
                .registrationStartTime(LocalDateTime.now().minusDays(2))
                .registrationEndTime(LocalDateTime.now().minusDays(1))
                .build();

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(courseRepository.findByIdAndDeletedFalse(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)).thenReturn(false);

        assertThatThrownBy(() -> enrollmentService.enrollStudent(dto))
                .isInstanceOf(FunctionalException.class)
                .hasMessageContaining("Registration for course 'Java' has already closed. It closed on")
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void getEnrollmentsByStudentReturnsMappedPageWhenStudentExists() {
        UUID studentId = UUID.randomUUID();
        PageRequest pageable = PageRequest.of(0, 10);
        Enrollment enrollment = Enrollment.builder().id(UUID.randomUUID()).build();
        EnrollmentResponseDto response = EnrollmentResponseDto.builder().id(enrollment.getId()).studentId(studentId).build();

        when(studentRepository.existsById(studentId)).thenReturn(true);
        when(enrollmentRepository.findAllByStudentId(studentId, pageable))
                .thenReturn(new PageImpl<>(List.of(enrollment), pageable, 1));
        when(enrollmentMapper.toResponseDto(enrollment)).thenReturn(response);

        Page<EnrollmentResponseDto> result = enrollmentService.getEnrollmentsByStudent(studentId, pageable);

        assertThat(result.getContent()).containsExactly(response);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void getEnrollmentsByStudentThrowsWhenStudentDoesNotExist() {
        UUID studentId = UUID.randomUUID();
        PageRequest pageable = PageRequest.of(0, 10);
        when(studentRepository.existsById(studentId)).thenReturn(false);

        assertThatThrownBy(() -> enrollmentService.getEnrollmentsByStudent(studentId, pageable))
                .isInstanceOf(FunctionalException.class)
                .hasMessage("Student not found with id: " + studentId)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void cancelEnrollmentMarksEnrollmentCancelled() {
        UUID enrollmentId = UUID.randomUUID();
        Enrollment enrollment = Enrollment.builder().id(enrollmentId).status(EnrollmentStatus.ACTIVE).build();
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));

        enrollmentService.cancelEnrollment(enrollmentId);

        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.CANCELLED);
        verify(enrollmentRepository).save(enrollment);
    }

    @Test
    void cancelEnrollmentThrowsWhenEnrollmentDoesNotExist() {
        UUID enrollmentId = UUID.randomUUID();
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.cancelEnrollment(enrollmentId))
                .isInstanceOf(FunctionalException.class)
                .hasMessage("Enrollment not found with id: " + enrollmentId)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private EnrollmentRequestDto enrollmentRequest(UUID studentId, UUID courseId) {
        return EnrollmentRequestDto.builder()
                .studentId(studentId)
                .courseId(courseId)
                .build();
    }

    private Course openCourse(UUID courseId) {
        return Course.builder()
                .id(courseId)
                .title("Java")
                .registrationStartTime(LocalDateTime.now().minusDays(1))
                .registrationEndTime(LocalDateTime.now().plusDays(1))
                .build();
    }
}
