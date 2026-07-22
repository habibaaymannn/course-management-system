package com.example.publicapi.serviceImplTest;

import com.example.core.entity.Course;
import com.example.core.entity.Enrollment;
import com.example.core.entity.Student;
import com.example.core.enums.EnrollmentStatus;
import com.example.core.exception.FunctionalException;
import com.example.publicapi.dto.request.EnrollmentRequestDto;
import com.example.publicapi.dto.response.EnrollmentResponseDto;
import com.example.publicapi.mapper.EnrollmentMapper;
import com.example.publicapi.repository.CourseRepository;
import com.example.publicapi.repository.EnrollmentRepository;
import com.example.publicapi.repository.StudentRepository;
import com.example.publicapi.serviceImpl.EnrollmentServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

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

    private final UUID studentId = UUID.randomUUID();
    private final Student authenticatedStudent = Student.builder()
            .id(studentId)
            .email("ada@example.com")
            .firstName("Ada")
            .lastName("Lovelace")
            .build();

    @BeforeEach
    void setAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("ada@example.com", null)
        );
    }

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void enrollStudentUsesAuthenticatedStudent() {
        UUID courseId = UUID.randomUUID();
        Course course = openCourse(courseId);
        Enrollment savedEnrollment = Enrollment.builder().id(UUID.randomUUID()).student(authenticatedStudent).course(course).build();
        EnrollmentResponseDto response = EnrollmentResponseDto.builder()
                .id(savedEnrollment.getId())
                .studentId(studentId)
                .courseId(courseId)
                .status(EnrollmentStatus.ACTIVE)
                .build();

        when(studentRepository.findByEmail("ada@example.com")).thenReturn(Optional.of(authenticatedStudent));
        when(courseRepository.findByIdAndDeletedFalse(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)).thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(savedEnrollment);
        when(enrollmentMapper.toResponseDto(savedEnrollment)).thenReturn(response);

        EnrollmentResponseDto result = enrollmentService.enrollStudent(enrollmentRequest(courseId));

        assertThat(result).isSameAs(response);
        ArgumentCaptor<Enrollment> enrollmentCaptor = ArgumentCaptor.forClass(Enrollment.class);
        verify(enrollmentRepository).save(enrollmentCaptor.capture());
        assertThat(enrollmentCaptor.getValue().getStudent()).isSameAs(authenticatedStudent);
        assertThat(enrollmentCaptor.getValue().getCourse()).isSameAs(course);
    }

    @Test
    void enrollStudentThrowsWhenAuthenticatedStudentDoesNotExist() {
        UUID courseId = UUID.randomUUID();
        when(studentRepository.findByEmail("ada@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.enrollStudent(enrollmentRequest(courseId)))
                .isInstanceOf(FunctionalException.class)
                .hasMessage("Authenticated student not found")
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.UNAUTHORIZED);

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void enrollStudentThrowsWhenCourseDoesNotExist() {
        UUID courseId = UUID.randomUUID();
        when(studentRepository.findByEmail("ada@example.com")).thenReturn(Optional.of(authenticatedStudent));
        when(courseRepository.findByIdAndDeletedFalse(courseId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.enrollStudent(enrollmentRequest(courseId)))
                .isInstanceOf(FunctionalException.class)
                .hasMessage("Course not found with id: " + courseId)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void enrollStudentThrowsWhenAlreadyEnrolled() {
        UUID courseId = UUID.randomUUID();
        Course course = openCourse(courseId);

        when(studentRepository.findByEmail("ada@example.com")).thenReturn(Optional.of(authenticatedStudent));
        when(courseRepository.findByIdAndDeletedFalse(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)).thenReturn(true);

        assertThatThrownBy(() -> enrollmentService.enrollStudent(enrollmentRequest(courseId)))
                .isInstanceOf(FunctionalException.class)
                .hasMessage("Student is already enrolled in this course")
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void enrollStudentThrowsWhenRegistrationWindowIsClosed() {
        UUID courseId = UUID.randomUUID();
        Course course = Course.builder()
                .id(courseId)
                .title("Java")
                .registrationStartTime(LocalDateTime.now().minusDays(2))
                .registrationEndTime(LocalDateTime.now().minusDays(1))
                .build();

        when(studentRepository.findByEmail("ada@example.com")).thenReturn(Optional.of(authenticatedStudent));
        when(courseRepository.findByIdAndDeletedFalse(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)).thenReturn(false);

        assertThatThrownBy(() -> enrollmentService.enrollStudent(enrollmentRequest(courseId)))
                .isInstanceOf(FunctionalException.class)
                .hasMessageContaining("Registration for course 'Java' has already closed. It closed on")
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getMyEnrollmentsReturnsAuthenticatedStudentsEnrollments() {
        PageRequest pageable = PageRequest.of(0, 10);
        Enrollment enrollment = Enrollment.builder().id(UUID.randomUUID()).student(authenticatedStudent).build();
        EnrollmentResponseDto response = EnrollmentResponseDto.builder().id(enrollment.getId()).studentId(studentId).build();

        when(studentRepository.findByEmail("ada@example.com")).thenReturn(Optional.of(authenticatedStudent));
        when(enrollmentRepository.findAllByStudentId(studentId, pageable))
                .thenReturn(new PageImpl<>(List.of(enrollment), pageable, 1));
        when(enrollmentMapper.toResponseDto(enrollment)).thenReturn(response);

        Page<EnrollmentResponseDto> result = enrollmentService.getMyEnrollments(pageable);

        assertThat(result.getContent()).containsExactly(response);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void cancelMyEnrollmentMarksOwnEnrollmentCancelled() {
        UUID enrollmentId = UUID.randomUUID();
        Enrollment enrollment = Enrollment.builder()
                .id(enrollmentId)
                .student(authenticatedStudent)
                .status(EnrollmentStatus.ACTIVE)
                .build();

        when(studentRepository.findByEmail("ada@example.com")).thenReturn(Optional.of(authenticatedStudent));
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));

        enrollmentService.cancelMyEnrollment(enrollmentId);

        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.CANCELLED);
        verify(enrollmentRepository).save(enrollment);
    }

    @Test
    void cancelMyEnrollmentRejectsAnotherStudentsEnrollment() {
        UUID enrollmentId = UUID.randomUUID();
        Student otherStudent = Student.builder().id(UUID.randomUUID()).build();
        Enrollment enrollment = Enrollment.builder().id(enrollmentId).student(otherStudent).build();

        when(studentRepository.findByEmail("ada@example.com")).thenReturn(Optional.of(authenticatedStudent));
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));

        assertThatThrownBy(() -> enrollmentService.cancelMyEnrollment(enrollmentId))
                .isInstanceOf(FunctionalException.class)
                .hasMessage("Enrollment does not belong to the authenticated student")
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    private EnrollmentRequestDto enrollmentRequest(UUID courseId) {
        return EnrollmentRequestDto.builder()
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
