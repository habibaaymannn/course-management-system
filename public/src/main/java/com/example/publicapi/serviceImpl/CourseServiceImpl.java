package com.example.publicapi.serviceImpl;

import com.example.publicapi.dto.response.CourseResponseDto;
import com.example.publicapi.exception.FunctionalException;
import com.example.publicapi.mapper.CourseMapper;
import com.example.publicapi.repository.CourseRepository;
import com.example.publicapi.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @Override
    public CourseResponseDto getCourseById(UUID id) {
        return courseMapper.toResponseDto(
                courseRepository.findByIdAndDeletedFalse(id)
                        .orElseThrow(() -> new FunctionalException("Course not found with id: " + id, HttpStatus.BAD_REQUEST)));
    }

    @Override
    public Page<CourseResponseDto> getAllCourses(Pageable pageable) {
        return courseRepository.findAllByDeletedFalse(pageable).map(courseMapper::toResponseDto);
    }

}
