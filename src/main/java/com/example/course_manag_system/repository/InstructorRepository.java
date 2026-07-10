package com.example.course_manag_system.repository;

import com.example.course_manag_system.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InstructorRepository extends JpaRepository<Instructor, UUID> {
    boolean existsByEmail(String email);
}
