package com.example.course_manag_system.service.serviceImpl;


import com.example.course_manag_system.dto.request.EnrollmentRequestDto;
import com.example.course_manag_system.dto.response.EnrollmentResponseDto;
import com.example.course_manag_system.entity.Course;
import com.example.course_manag_system.entity.Enrollment;
import com.example.course_manag_system.entity.EnrollmentStatus;
import com.example.course_manag_system.entity.Student;
import com.example.course_manag_system.exception.FunctionalException;
import com.example.course_manag_system.mapper.EnrollmentMapper;
import com.example.course_manag_system.repository.CourseRepository;
import com.example.course_manag_system.repository.EnrollmentRepository;
import com.example.course_manag_system.repository.StudentRepository;
import com.example.course_manag_system.service.EnrollmentService;
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
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentMapper enrollmentMapper;

    @Override
    public EnrollmentResponseDto enrollStudent(EnrollmentRequestDto dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new FunctionalException("Student not found with id: " + dto.getStudentId(), HttpStatus.BAD_REQUEST));

        // A student can only be enrolled in an active (non soft-deleted) course.
        Course course = courseRepository.findByIdAndDeletedFalse(dto.getCourseId())
                .orElseThrow(() -> new FunctionalException("Course not found with id: " + dto.getCourseId(), HttpStatus.BAD_REQUEST));

        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), course.getId())) {
            throw new FunctionalException("Student is already enrolled in this course", HttpStatus.BAD_REQUEST);
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .build();

        return enrollmentMapper.toResponseDto(enrollmentRepository.save(enrollment));
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentResponseDto getEnrollmentById(UUID id) {
        return enrollmentMapper.toResponseDto(findEnrollmentOrThrow(id));
    }

    @Override
    public Page<EnrollmentResponseDto> getEnrollmentsByStudent(UUID studentId, Pageable pageable) {
        if (!studentRepository.existsById(studentId)) {
            throw new FunctionalException("Student not found with id: " + studentId, HttpStatus.BAD_REQUEST);
        }
        return enrollmentRepository.findAllByStudentId(studentId, pageable).map(enrollmentMapper::toResponseDto);
    }

    @Override
    public Page<EnrollmentResponseDto> getEnrollmentsByCourse(UUID courseId, Pageable pageable) {
        if (!courseRepository.existsById(courseId)) {
            throw new FunctionalException("Course not found with id: " + courseId, HttpStatus.BAD_REQUEST);
        }
        return enrollmentRepository.findAllByCourseId(courseId, pageable).map(enrollmentMapper::toResponseDto);
    }

    @Override
    public EnrollmentResponseDto updateEnrollmentStatus(UUID id, EnrollmentStatus status) {
        Enrollment enrollment = findEnrollmentOrThrow(id);
        enrollment.setStatus(status);
        return enrollmentMapper.toResponseDto(enrollmentRepository.save(enrollment));
    }

    @Override
    public void cancelEnrollment(UUID id) {
        Enrollment enrollment = findEnrollmentOrThrow(id);
        enrollment.setStatus(EnrollmentStatus.CANCELLED);
        enrollmentRepository.save(enrollment);
    }

    private Enrollment findEnrollmentOrThrow(UUID id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new FunctionalException("Enrollment not found with id: " + id, HttpStatus.BAD_REQUEST));
    }
}
