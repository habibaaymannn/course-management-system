package com.example.admin.serviceImpl;

import com.example.admin.dto.response.SystemReportDto;
import com.example.core.enums.EnrollmentStatus;
import com.example.admin.repository.CourseRepository;
import com.example.admin.repository.EnrollmentRepository;
import com.example.admin.repository.InstructorRepository;
import com.example.admin.repository.StudentRepository;
import com.example.admin.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public SystemReportDto getSystemReport() {
        long activeEnrollments = enrollmentRepository.countByStatus(EnrollmentStatus.ACTIVE);
        long completedEnrollments = enrollmentRepository.countByStatus(EnrollmentStatus.COMPLETED);
        long cancelledEnrollments = enrollmentRepository.countByStatus(EnrollmentStatus.CANCELLED);

        return SystemReportDto.builder()
                .totalActiveCourses(courseRepository.countByDeletedFalse())
                .totalDeletedCourses(courseRepository.countByDeletedTrue())
                .totalInstructors(instructorRepository.count())
                .totalStudents(studentRepository.count())
                .totalEnrollments(activeEnrollments + completedEnrollments + cancelledEnrollments)
                .activeEnrollments(activeEnrollments)
                .completedEnrollments(completedEnrollments)
                .cancelledEnrollments(cancelledEnrollments)
                .build();
    }
}
