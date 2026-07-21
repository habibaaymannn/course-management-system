package com.example.admin.repository;

import com.example.core.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

// Read-only usage from the admin service (reporting only).
public interface StudentRepository extends JpaRepository<Student, UUID> {
}
