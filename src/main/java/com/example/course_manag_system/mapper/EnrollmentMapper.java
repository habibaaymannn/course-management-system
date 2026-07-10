package com.example.course_manag_system.mapper;


import com.example.course_manag_system.dto.response.EnrollmentResponseDto;
import com.example.course_manag_system.entity.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    // Enrollment is always built from freshly-fetched Student/Course entities in the
    // service layer (it needs repository lookups + duplicate/soft-delete checks first),
    // so only the entity -> response direction is generated here.
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", expression =
            "java(enrollment.getStudent().getFirstName() + \" \" + enrollment.getStudent().getLastName())")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseTitle", source = "course.title")
    EnrollmentResponseDto toResponseDto(Enrollment enrollment);
}
