package com.example.publicapi.serviceImplTest;

import com.example.publicapi.dto.request.StudentRequestDto;
import com.example.publicapi.dto.response.StudentResponseDto;
import com.example.publicapi.entity.Student;
import com.example.publicapi.exception.FunctionalException;
import com.example.publicapi.mapper.StudentMapper;
import com.example.publicapi.repository.StudentRepository;
import com.example.publicapi.serviceImpl.StudentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock private StudentRepository studentRepository;
    @Mock private StudentMapper studentMapper;
    @InjectMocks private StudentServiceImpl studentService;

    @Test
    void createStudentSavesStudentWhenEmailIsUnique() {
        StudentRequestDto dto = studentRequest("ada@example.com");
        Student mappedStudent = Student.builder().email(dto.getEmail()).build();
        Student savedStudent = Student.builder().id(UUID.randomUUID()).email(dto.getEmail()).build();
        StudentResponseDto response = StudentResponseDto.builder()
                .id(savedStudent.getId())
                .email(dto.getEmail())
                .build();

        when(studentRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(studentMapper.toEntity(dto)).thenReturn(mappedStudent);
        when(studentRepository.save(mappedStudent)).thenReturn(savedStudent);
        when(studentMapper.toResponseDto(savedStudent)).thenReturn(response);

        StudentResponseDto result = studentService.createStudent(dto);

        assertThat(result).isSameAs(response);
        verify(studentRepository).save(mappedStudent);
    }

    @Test
    void createStudentThrowsWhenEmailAlreadyExists() {
        StudentRequestDto dto = studentRequest("ada@example.com");
        when(studentRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> studentService.createStudent(dto))
                .isInstanceOf(FunctionalException.class)
                .hasMessage("Student with email 'ada@example.com' already exists")
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(studentRepository, never()).save(any());
    }

    @Test
    void getStudentByIdReturnsMappedStudent() {
        UUID studentId = UUID.randomUUID();
        Student student = Student.builder().id(studentId).email("ada@example.com").build();
        StudentResponseDto response = StudentResponseDto.builder().id(studentId).email("ada@example.com").build();

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(studentMapper.toResponseDto(student)).thenReturn(response);

        StudentResponseDto result = studentService.getStudentById(studentId);

        assertThat(result).isSameAs(response);
    }

    @Test
    void getStudentByIdThrowsWhenStudentDoesNotExist() {
        UUID studentId = UUID.randomUUID();
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getStudentById(studentId))
                .isInstanceOf(FunctionalException.class)
                .hasMessage("Student not found with id: " + studentId)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private StudentRequestDto studentRequest(String email) {
        return StudentRequestDto.builder()
                .firstName("Ada")
                .lastName("Lovelace")
                .email(email)
                .build();
    }
}
