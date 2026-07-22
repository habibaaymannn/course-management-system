package com.example.admin.serviceImplTest;

import com.example.admin.dto.response.SystemReportDto;
import com.example.core.enums.EnrollmentStatus;
import com.example.admin.repository.CourseRepository;
import com.example.admin.repository.EnrollmentRepository;
import com.example.admin.repository.InstructorRepository;
import com.example.admin.repository.StudentRepository;
import com.example.admin.serviceImpl.ReportServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock private CourseRepository courseRepository;
    @Mock private InstructorRepository instructorRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private EnrollmentRepository enrollmentRepository;
    @InjectMocks private ReportServiceImpl reportService;

    @Test
    void getSystemReportReturnsCountsFromRepositories() {
        when(courseRepository.countByDeletedFalse()).thenReturn(7L);
        when(courseRepository.countByDeletedTrue()).thenReturn(2L);
        when(instructorRepository.count()).thenReturn(4L);
        when(studentRepository.count()).thenReturn(15L);
        when(enrollmentRepository.countByStatus(EnrollmentStatus.ACTIVE)).thenReturn(9L);
        when(enrollmentRepository.countByStatus(EnrollmentStatus.COMPLETED)).thenReturn(5L);
        when(enrollmentRepository.countByStatus(EnrollmentStatus.CANCELLED)).thenReturn(3L);

        SystemReportDto result = reportService.getSystemReport();

        assertThat(result.getTotalActiveCourses()).isEqualTo(7L);
        assertThat(result.getTotalDeletedCourses()).isEqualTo(2L);
        assertThat(result.getTotalInstructors()).isEqualTo(4L);
        assertThat(result.getTotalStudents()).isEqualTo(15L);
        assertThat(result.getActiveEnrollments()).isEqualTo(9L);
        assertThat(result.getCompletedEnrollments()).isEqualTo(5L);
        assertThat(result.getCancelledEnrollments()).isEqualTo(3L);
        assertThat(result.getTotalEnrollments()).isEqualTo(17L);
    }
}
