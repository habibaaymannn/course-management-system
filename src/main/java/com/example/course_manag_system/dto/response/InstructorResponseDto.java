package com.example.course_manag_system.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorResponseDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String department;
    private int activeCourseCount;
}
