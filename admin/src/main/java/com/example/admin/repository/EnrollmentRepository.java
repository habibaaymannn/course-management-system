package com.example.admin.repository;

import com.example.admin.entity.Enrollment;
import com.example.admin.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

// Read-only usage from the admin service (reporting only).
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    long countByStatus(EnrollmentStatus status);
}
