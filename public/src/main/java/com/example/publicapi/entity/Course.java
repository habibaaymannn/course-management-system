package com.example.publicapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Mirrors the "courses" table, owned/created by the admin service (this
// service only reads courses and validates the registration window - it
// never creates, updates, or deletes a course).
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

    private LocalDateTime registrationStartTime;
    private LocalDateTime registrationEndTime;

    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Enrollment> enrollments = new ArrayList<>();

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
