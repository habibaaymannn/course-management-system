package com.example.publicapi.serviceImpl;

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
import com.example.publicapi.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentMapper enrollmentMapper;

    @Override
    public EnrollmentResponseDto enrollStudent(EnrollmentRequestDto dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new FunctionalException("Student not found with id: " + dto.getStudentId(), HttpStatus.BAD_REQUEST));

        Course course = courseRepository.findByIdAndDeletedFalse(dto.getCourseId())
                .orElseThrow(() -> new FunctionalException("Course not found with id: " + dto.getCourseId(), HttpStatus.BAD_REQUEST));

        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), course.getId())) {
            throw new FunctionalException("Student is already enrolled in this course", HttpStatus.BAD_REQUEST);
        }

        validateRegistrationWindow(course);

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .build();

        return enrollmentMapper.toResponseDto(enrollmentRepository.save(enrollment));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnrollmentResponseDto> getEnrollmentsByStudent(UUID studentId, Pageable pageable) {
        if (!studentRepository.existsById(studentId)) {
            throw new FunctionalException("Student not found with id: " + studentId, HttpStatus.BAD_REQUEST);
        }
        return enrollmentRepository.findAllByStudentId(studentId, pageable).map(enrollmentMapper::toResponseDto);
    }

    @Override
    public void cancelEnrollment(UUID id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new FunctionalException("Enrollment not found with id: " + id, HttpStatus.BAD_REQUEST));
        enrollment.setStatus(EnrollmentStatus.CANCELLED);
        enrollmentRepository.save(enrollment);
    }

    // Core new business rule for v2.0: enrollment is only allowed while
    // now() falls within [registrationStartTime, registrationEndTime].
    private void validateRegistrationWindow(Course course) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = course.getRegistrationStartTime();
        LocalDateTime end = course.getRegistrationEndTime();

        if (start == null || end == null) {
            throw new FunctionalException("Registration for course '" + course.getTitle() + "' has not been configured yet", HttpStatus.BAD_REQUEST);
        }
        if (now.isBefore(start)) {
            throw new FunctionalException(
                    "Registration for course '" + course.getTitle() + "' has not opened yet. It opens on "
                            + start.format(DISPLAY_FORMAT), HttpStatus.BAD_REQUEST);
        }
        if (now.isAfter(end)) {
            throw new FunctionalException(
                    "Registration for course '" + course.getTitle() + "' has already closed. It closed on "
                            + end.format(DISPLAY_FORMAT), HttpStatus.BAD_REQUEST);
        }
    }
}
