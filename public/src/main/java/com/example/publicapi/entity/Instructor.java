package com.example.publicapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

// Read-only mirror of the "instructors" table, owned by the admin service.
// The public service only needs this to display the instructor's name
// alongside a course listing.
@Entity
@Table(name = "instructors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    private String department;
}
