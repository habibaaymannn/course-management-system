package com.example.course_manag_system.dto.response;

import com.example.course_manag_system.entity.EnrollmentStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentResponseDto {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseTitle;
    private LocalDate enrollmentDate;
    private EnrollmentStatus status;
}
