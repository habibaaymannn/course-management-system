package com.example.publicapi.repository;

import com.example.core.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

// Read-only usage from the public service (displaying instructor names on courses).
public interface InstructorRepository extends JpaRepository<Instructor, UUID> {
}
