package com.example.admin.repository;

import com.example.admin.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {

    Page<Course> findAllByDeletedFalse(Pageable pageable);

    Optional<Course> findByIdAndDeletedFalse(UUID id);

    long countByDeletedFalse();

    long countByDeletedTrue();
}
