package com.example.publicapi.mapper;

import com.example.publicapi.dto.response.StudentResponseDto;
import com.example.core.entity.Student;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "enrollmentCount", expression =
            "java(student.getEnrollments() == null ? 0 : student.getEnrollments().size())")
    StudentResponseDto toResponseDto(Student student);
}
