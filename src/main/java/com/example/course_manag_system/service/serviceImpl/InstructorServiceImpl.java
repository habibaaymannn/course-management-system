package com.example.course_manag_system.service.serviceImpl;

import com.example.course_manag_system.dto.request.InstructorRequestDto;
import com.example.course_manag_system.dto.response.InstructorResponseDto;
import com.example.course_manag_system.entity.Instructor;
import com.example.course_manag_system.exception.FunctionalException;
import com.example.course_manag_system.mapper.InstructorMapper;
import com.example.course_manag_system.repository.InstructorRepository;
import com.example.course_manag_system.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository instructorRepository;
    private final InstructorMapper instructorMapper;

    @Override
    public InstructorResponseDto createInstructor(InstructorRequestDto dto) {
        if (instructorRepository.existsByEmail(dto.getEmail())) {
            throw new FunctionalException("Instructor with email '" + dto.getEmail() + "' already exists", HttpStatus.BAD_REQUEST);
        }
        Instructor instructor = instructorMapper.toInstructor(dto);
        return instructorMapper.toResponseDto(instructorRepository.save(instructor));
    }

    @Override
    public InstructorResponseDto getInstructorById(UUID id) {
        return instructorMapper.toResponseDto(findInstructorOrThrow(id));
    }

    @Override
    public Page<InstructorResponseDto> getAllInstructors(Pageable pageable) {
        return instructorRepository.findAll(pageable).map(instructorMapper::toResponseDto);
    }

    @Override
    public InstructorResponseDto updateInstructor(UUID id, InstructorRequestDto dto) {
        Instructor instructor = findInstructorOrThrow(id);

        if (!instructor.getEmail().equalsIgnoreCase(dto.getEmail())
                && instructorRepository.existsByEmail(dto.getEmail())) {
            throw new FunctionalException("Instructor with email '" + dto.getEmail() + "' already exists", HttpStatus.BAD_REQUEST);
        }

        instructorMapper.updateEntityFromDto(dto, instructor);
        return instructorMapper.toResponseDto(instructorRepository.save(instructor));
    }

    @Override
    public void deleteInstructor(UUID id) {
        Instructor instructor = findInstructorOrThrow(id);
        instructorRepository.delete(instructor);
    }

    private Instructor findInstructorOrThrow(UUID id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new FunctionalException("Instructor not found with id: " + id, HttpStatus.BAD_REQUEST));
    }
}
