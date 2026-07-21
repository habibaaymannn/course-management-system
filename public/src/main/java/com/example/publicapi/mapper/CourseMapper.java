package com.example.publicapi.mapper;

import com.example.publicapi.dto.response.CourseResponseDto;
import com.example.core.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(target = "instructorName", expression = "java(course.getInstructor() != null ? course.getInstructor().getFirstName() + \" \" + course.getInstructor().getLastName() : null)")
    @Mapping(target = "registrationOpen", expression = "java(course.isRegistrationOpen(java.time.LocalDateTime.now()))")
    CourseResponseDto toResponseDto(Course course);
}
