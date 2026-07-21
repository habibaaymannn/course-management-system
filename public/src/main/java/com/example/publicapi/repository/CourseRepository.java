package com.example.publicapi.repository;

import com.example.core.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

// This service only ever reads courses - never writes. All finders filter
// out soft-deleted courses, mirroring the admin service's soft-delete rule.
public interface CourseRepository extends JpaRepository<Course, UUID> {

    Page<Course> findAllByDeletedFalse(Pageable pageable);
    Optional<Course> findByIdAndDeletedFalse(UUID id);

}
