package com.coursemanagement.repository;

import com.coursemanagement.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // Soft-delete aware finders. All read paths must go through these
    // so that logically deleted courses never surface in the API.

    Page<Course> findAllByDeletedFalse(Pageable pageable);

    Optional<Course> findByIdAndDeletedFalse(Long id);

    List<Course> findAllByInstructorIdAndDeletedFalse(Long instructorId);

    @Query("SELECT c FROM Course c WHERE c.deleted = false AND " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Course> searchByTitle(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByIdAndDeletedFalse(Long id);
}
