package com.example.publicapi.serviceImpl;

import com.example.publicapi.dto.request.StudentRequestDto;
import com.example.publicapi.dto.response.StudentResponseDto;
import com.example.core.entity.Student;
import com.example.core.exception.FunctionalException;
import com.example.publicapi.mapper.StudentMapper;
import com.example.publicapi.repository.StudentRepository;
import com.example.publicapi.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Override
    public StudentResponseDto createStudent(StudentRequestDto dto) {
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new FunctionalException("Student with email '" + dto.getEmail() + "' already exists", HttpStatus.BAD_REQUEST);
        }
        Student student = studentMapper.toEntity(dto);
        return studentMapper.toResponseDto(studentRepository.save(student));
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDto getStudentById(UUID id) {
        return studentMapper.toResponseDto(findStudentOrThrow(id));
    }


    private Student findStudentOrThrow(UUID id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new FunctionalException("Student not found with id: " + id, HttpStatus.BAD_REQUEST));
    }
}
