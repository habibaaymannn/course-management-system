package com.example.course_manag_system.service.serviceImpl;

import com.example.course_manag_system.dto.request.StudentRequestDto;
import com.example.course_manag_system.dto.response.StudentResponseDto;
import com.example.course_manag_system.entity.Student;
import com.example.course_manag_system.exception.FunctionalException;
import com.example.course_manag_system.mapper.StudentMapper;
import com.example.course_manag_system.repository.StudentRepository;
import com.example.course_manag_system.service.StudentService;
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
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Override
    public StudentResponseDto createStudent(StudentRequestDto dto) {
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new FunctionalException("Student with email '" + dto.getEmail() + "' already exists", HttpStatus.BAD_REQUEST);
        }
        Student student = studentMapper.toStudent(dto);
        return studentMapper.toResponseDto(studentRepository.save(student));
    }

    @Override
    public StudentResponseDto getStudentById(UUID id) {
        return studentMapper.toResponseDto(findStudent(id));
    }

    @Override
    public Page<StudentResponseDto> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable).map(studentMapper::toResponseDto);
    }

    @Override
    public StudentResponseDto updateStudent(UUID id, StudentRequestDto dto) {
        Student student = findStudent(id);
        if (!student.getEmail().equalsIgnoreCase(dto.getEmail())
                && studentRepository.existsByEmail(dto.getEmail())) {
            throw new FunctionalException("Student with email '" + dto.getEmail() + "' already exists", HttpStatus.BAD_REQUEST);
        }

        studentMapper.updateEntityFromDto(dto, student);
        return studentMapper.toResponseDto(studentRepository.save(student));
    }

    @Override
    public void deleteStudent(UUID id) {
        Student student = findStudent(id);
        studentRepository.delete(student);
    }

    private Student findStudent(UUID id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new FunctionalException("Student not found with id: " + id, HttpStatus.BAD_REQUEST));
    }
}
