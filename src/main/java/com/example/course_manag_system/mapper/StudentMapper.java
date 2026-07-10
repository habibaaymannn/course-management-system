package com.example.course_manag_system.mapper;


import com.example.course_manag_system.dto.request.StudentRequestDto;
import com.example.course_manag_system.dto.response.StudentResponseDto;
import com.example.course_manag_system.entity.Student;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    Student toStudent(StudentRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    void updateEntityFromDto(StudentRequestDto dto, @MappingTarget Student student);

    @Mapping(target = "enrollmentCount", expression =
            "java(student.getEnrollments() == null ? 0 : student.getEnrollments().size())")
    StudentResponseDto toResponseDto(Student student);
}
