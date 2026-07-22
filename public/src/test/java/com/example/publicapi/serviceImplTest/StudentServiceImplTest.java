package com.example.publicapi.serviceImplTest;

import com.example.core.entity.Student;
import com.example.core.exception.FunctionalException;
import com.example.publicapi.dto.response.StudentResponseDto;
import com.example.publicapi.mapper.StudentMapper;
import com.example.publicapi.repository.StudentRepository;
import com.example.publicapi.serviceImpl.StudentServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock private StudentRepository studentRepository;
    @Mock private StudentMapper studentMapper;
    @InjectMocks private StudentServiceImpl studentService;

    @BeforeEach
    void setAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("ada@example.com", null)
        );
    }

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentStudentReturnsMappedAuthenticatedStudent() {
        Student student = Student.builder().id(UUID.randomUUID()).email("ada@example.com").build();
        StudentResponseDto response = StudentResponseDto.builder()
                .id(student.getId())
                .email("ada@example.com")
                .build();

        when(studentRepository.findByEmail("ada@example.com")).thenReturn(Optional.of(student));
        when(studentMapper.toResponseDto(student)).thenReturn(response);

        StudentResponseDto result = studentService.getCurrentStudent();

        assertThat(result).isSameAs(response);
    }

    @Test
    void getCurrentStudentThrowsWhenAuthenticatedStudentDoesNotExist() {
        when(studentRepository.findByEmail("ada@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getCurrentStudent())
                .isInstanceOf(FunctionalException.class)
                .hasMessage("Authenticated student not found")
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
