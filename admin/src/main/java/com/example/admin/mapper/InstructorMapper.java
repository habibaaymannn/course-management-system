package com.example.admin.mapper;


import com.example.admin.dto.request.InstructorRequestDto;
import com.example.admin.dto.response.InstructorResponseDto;
import com.example.admin.entity.Course;
import com.example.admin.entity.Instructor;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InstructorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courses", ignore = true)
    Instructor toEntity(InstructorRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courses", ignore = true)
    void updateEntityFromDto(InstructorRequestDto dto, @MappingTarget Instructor instructor);

    @Mapping(target = "activeCourseCount", expression =
            "java(instructor.getCourses() == null ? 0 : (int) instructor.getCourses().stream().filter(c -> !c.isDeleted()).count())")
    InstructorResponseDto toResponseDto(Instructor instructor);
}
