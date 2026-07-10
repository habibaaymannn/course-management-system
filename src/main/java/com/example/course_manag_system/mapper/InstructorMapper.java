package com.example.course_manag_system.mapper;


import com.example.course_manag_system.dto.request.InstructorRequestDto;
import com.example.course_manag_system.dto.response.InstructorResponseDto;
import com.example.course_manag_system.entity.Instructor;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface InstructorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courses", ignore = true)
    Instructor toInstructor(InstructorRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courses", ignore = true)
    void updateEntityFromDto(InstructorRequestDto dto, @MappingTarget Instructor instructor);

    @Mapping(target = "activeCourseCount", expression =
            "java(instructor.getCourses() == null ? 0 : (int) instructor.getCourses().stream().filter(c -> !c.isDeleted()).count())")
    InstructorResponseDto toResponseDto(Instructor instructor);
}
