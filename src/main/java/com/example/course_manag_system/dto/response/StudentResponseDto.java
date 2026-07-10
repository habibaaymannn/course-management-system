package com.example.course_manag_system.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private int enrollmentCount;
}
