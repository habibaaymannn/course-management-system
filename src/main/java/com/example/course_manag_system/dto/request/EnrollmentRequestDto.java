package com.example.course_manag_system.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentRequestDto {
    @NotNull(message = "Student id is required")
    private UUID studentId;
    @NotNull(message = "Course id is required")
    private UUID courseId;
}
