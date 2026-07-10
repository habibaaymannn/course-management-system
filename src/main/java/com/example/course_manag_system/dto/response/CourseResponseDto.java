package com.example.course_manag_system.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponseDto {
    private UUID id;
    private String title;
    private String description;
    private Integer credits;
    private UUID instructorId;
    private String instructorName;
    private int enrolledStudentCount;
}
