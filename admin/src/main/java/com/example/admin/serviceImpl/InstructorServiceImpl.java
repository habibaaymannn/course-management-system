package com.example.admin.serviceImpl;

import com.example.admin.dto.request.InstructorRequestDto;
import com.example.admin.dto.response.InstructorResponseDto;
import com.example.core.entity.Instructor;
import com.example.core.exception.FunctionalException;
import com.example.admin.mapper.InstructorMapper;
import com.example.admin.repository.InstructorRepository;
import com.example.admin.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository instructorRepository;
    private final InstructorMapper instructorMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public InstructorResponseDto createInstructor(InstructorRequestDto dto) {
        if (instructorRepository.existsByEmail(dto.getEmail())) {
            throw new FunctionalException("Instructor with email '" + dto.getEmail() + "' already exists", HttpStatus.BAD_REQUEST);
        }
        validatePassword(dto.getPassword());
        Instructor instructor = instructorMapper.toEntity(dto);
        instructor.setPassword(passwordEncoder.encode(dto.getPassword()));
        return instructorMapper.toResponseDto(instructorRepository.save(instructor));
    }

    @Override
    @Transactional(readOnly = true)
    public InstructorResponseDto getInstructorById(UUID id) {
        return instructorMapper.toResponseDto(findInstructorOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
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
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            validatePassword(dto.getPassword());
            instructor.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return instructorMapper.toResponseDto(instructorRepository.save(instructor));
    }

    @Override
    public void deleteInstructor(UUID id) {
        Instructor instructor = findInstructorOrThrow(id);
        instructorRepository.delete(instructor);
    }

    private Instructor findInstructorOrThrow(UUID id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new FunctionalException("Instructor not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new FunctionalException("Password must be at least 8 characters", HttpStatus.BAD_REQUEST);
        }
    }
}
