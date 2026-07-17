package com.example.admin.service;


import com.example.admin.dto.request.InstructorRequestDto;
import com.example.admin.dto.response.InstructorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface InstructorService {
    InstructorResponseDto createInstructor(InstructorRequestDto dto);
    InstructorResponseDto getInstructorById(UUID id);
    Page<InstructorResponseDto> getAllInstructors(Pageable pageable);
    InstructorResponseDto updateInstructor(UUID id, InstructorRequestDto dto);
    void deleteInstructor(UUID id);
}
