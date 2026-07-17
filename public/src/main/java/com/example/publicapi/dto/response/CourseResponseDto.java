package com.example.publicapi.dto.response;

import lombok.*;

import java.time.LocalDateTime;
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
    private String instructorName;
    private LocalDateTime registrationStartTime;
    private LocalDateTime registrationEndTime;
    private boolean registrationOpen;
}
