package com.example.course_manag_system.repository;

import com.example.course_manag_system.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID> {
    boolean existsByEmail(String email);
}
