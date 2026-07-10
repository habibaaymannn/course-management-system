package com.example.course_manag_system.dto.response;

import com.example.course_manag_system.entity.EnrollmentStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentResponseDto {
    private UUID id;
    private UUID studentId;
    private String studentName;
    private UUID courseId;
    private String courseTitle;
    private LocalDate enrollmentDate;
    private EnrollmentStatus status;
}
