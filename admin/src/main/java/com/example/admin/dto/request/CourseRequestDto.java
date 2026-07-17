package com.example.admin.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Credits is required")
    @Min(value = 1, message = "Credits must be at least 1")
    private Integer credits;

    // Optional: assign an instructor at creation/update time
    private UUID instructorId;

    @NotNull(message = "Registration start time is required")
    private LocalDateTime registrationStartTime;

    @NotNull(message = "Registration end time is required")
    private LocalDateTime registrationEndTime;
}
