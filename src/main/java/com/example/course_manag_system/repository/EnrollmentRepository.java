package com.example.course_manag_system.repository;

import com.example.course_manag_system.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

    Page<Enrollment> findAllByStudentId(UUID studentId, Pageable pageable);
    Page<Enrollment> findAllByCourseId(UUID courseId, Pageable pageable);
    boolean existsByStudentIdAndCourseId(UUID studentId, UUID courseId);

}
