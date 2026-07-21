package com.example.admin.mapper;


import com.example.admin.dto.request.CourseRequestDto;
import com.example.admin.dto.response.CourseResponseDto;
import com.example.core.entity.Course;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "instructor", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Course toEntity(CourseRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "instructor", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntityFromDto(CourseRequestDto dto, @MappingTarget Course course);

    @Mapping(target = "instructorId", expression = "java(course.getInstructor() != null ? course.getInstructor().getId() : null)")
    @Mapping(target = "instructorName", expression = "java(course.getInstructor() != null ? course.getInstructor().getFirstName() + \" \" + course.getInstructor().getLastName() : null)")
    @Mapping(target = "enrolledStudentCount", expression = "java(course.getEnrollments() == null ? 0 : course.getEnrollments().size())")
    CourseResponseDto toResponseDto(Course course);
}
