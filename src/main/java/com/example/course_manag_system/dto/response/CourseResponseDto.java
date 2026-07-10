package com.coursemanagement.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponseDto {
    private Long id;
    private String title;
    private String description;
    private Integer credits;
    private Long instructorId;
    private String instructorName;
    private int enrolledStudentCount;
}
