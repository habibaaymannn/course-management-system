package com.example.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Integer credits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    // Registration window - students may only enroll (via the public service)
    // while now() falls between these two timestamps. Set/managed here in
    // the admin service.
    private LocalDateTime registrationStartTime;
    private LocalDateTime registrationEndTime;

    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Enrollment> enrollments = new ArrayList<>();

    // Soft delete flag - courses are never physically removed via the API,
    // they are marked deleted=true instead.
    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false;

    // Convenience check used by the enrollment service - true only while
    // "now" falls within [registrationStartTime, registrationEndTime].
    public boolean isRegistrationOpen(LocalDateTime now) {
        return registrationStartTime != null && registrationEndTime != null
                && !now.isBefore(registrationStartTime)
                && !now.isAfter(registrationEndTime);
    }
}
