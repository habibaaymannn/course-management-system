package com.example.course_manag_system.repository;

import com.example.course_manag_system.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {

    // Soft-delete aware finders. All read paths must go through these
    // so that logically deleted courses never surface in the API.

    Page<Course> findAllByDeletedFalse(Pageable pageable);

    Optional<Course> findByIdAndDeletedFalse(UUID id);
}
