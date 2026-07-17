package com.example.admin.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemReportDto {
    private long totalActiveCourses;
    private long totalDeletedCourses;
    private long totalInstructors;
    private long totalStudents;
    private long totalEnrollments;
    private long activeEnrollments;
    private long completedEnrollments;
    private long cancelledEnrollments;
}
