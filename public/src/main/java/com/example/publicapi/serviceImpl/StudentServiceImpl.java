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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDto getCurrentStudent() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new FunctionalException("Authenticated student not found", HttpStatus.UNAUTHORIZED));
        return studentMapper.toResponseDto(student);
    }
}
