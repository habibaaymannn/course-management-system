package com.example.publicapi.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentRequestDto {

    @NotNull(message = "Course id is required")
    private UUID courseId;
}
