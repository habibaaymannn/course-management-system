package com.example.admin.serviceImplTest;

import com.example.admin.dto.request.InstructorRequestDto;
import com.example.admin.dto.response.InstructorResponseDto;
import com.example.admin.entity.Instructor;
import com.example.admin.exception.FunctionalException;
import com.example.admin.mapper.InstructorMapper;
import com.example.admin.repository.InstructorRepository;
import com.example.admin.serviceImpl.InstructorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstructorServiceImplTest {

    @Mock private InstructorRepository instructorRepository;
    @Mock private InstructorMapper instructorMapper;
    @InjectMocks private InstructorServiceImpl instructorService;

    @Test
    void createInstructorSavesInstructorWhenEmailIsUnique() {
        InstructorRequestDto dto = instructorRequest("ada@example.com");
        Instructor mappedInstructor = Instructor.builder().email(dto.getEmail()).build();
        Instructor savedInstructor = Instructor.builder().id(UUID.randomUUID()).email(dto.getEmail()).build();
        InstructorResponseDto response = InstructorResponseDto.builder()
                .id(savedInstructor.getId())
                .email(dto.getEmail())
                .build();

        when(instructorRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(instructorMapper.toEntity(dto)).thenReturn(mappedInstructor);
        when(instructorRepository.save(mappedInstructor)).thenReturn(savedInstructor);
        when(instructorMapper.toResponseDto(savedInstructor)).thenReturn(response);

        InstructorResponseDto result = instructorService.createInstructor(dto);

        assertThat(result).isSameAs(response);
        verify(instructorRepository).save(mappedInstructor);
    }

    @Test
    void createInstructorThrowsWhenEmailAlreadyExists() {
        InstructorRequestDto dto = instructorRequest("ada@example.com");
        when(instructorRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> instructorService.createInstructor(dto))
                .isInstanceOf(FunctionalException.class)
                .hasMessage("Instructor with email 'ada@example.com' already exists")
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        verify(instructorRepository, never()).save(any());
    }

    @Test
    void getInstructorByIdReturnsMappedInstructor() {
        UUID instructorId = UUID.randomUUID();
        Instructor instructor = Instructor.builder().id(instructorId).email("ada@example.com").build();
        InstructorResponseDto response = InstructorResponseDto.builder().id(instructorId).email("ada@example.com").build();

        when(instructorRepository.findById(instructorId)).thenReturn(Optional.of(instructor));
        when(instructorMapper.toResponseDto(instructor)).thenReturn(response);

        InstructorResponseDto result = instructorService.getInstructorById(instructorId);

        assertThat(result).isSameAs(response);
    }

    @Test
    void getInstructorByIdThrowsWhenInstructorDoesNotExist() {
        UUID instructorId = UUID.randomUUID();
        when(instructorRepository.findById(instructorId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> instructorService.getInstructorById(instructorId))
                .isInstanceOf(FunctionalException.class)
                .hasMessage("Instructor not found with id: " + instructorId)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getAllInstructorsReturnsMappedPage() {
        PageRequest pageable = PageRequest.of(0, 10);
        Instructor instructor = Instructor.builder().id(UUID.randomUUID()).email("ada@example.com").build();
        InstructorResponseDto response = InstructorResponseDto.builder()
                .id(instructor.getId())
                .email("ada@example.com")
                .build();

        when(instructorRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(instructor), pageable, 1));
        when(instructorMapper.toResponseDto(instructor)).thenReturn(response);

        Page<InstructorResponseDto> result = instructorService.getAllInstructors(pageable);

        assertThat(result.getContent()).containsExactly(response);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void updateInstructorAllowsSameEmailIgnoringCase() {
        UUID instructorId = UUID.randomUUID();
        InstructorRequestDto dto = instructorRequest("ADA@example.com");
        Instructor existingInstructor = Instructor.builder().id(instructorId).email("ada@example.com").build();
        Instructor savedInstructor = Instructor.builder().id(instructorId).email("ADA@example.com").build();
        InstructorResponseDto response = InstructorResponseDto.builder().id(instructorId).email("ADA@example.com").build();

        when(instructorRepository.findById(instructorId)).thenReturn(Optional.of(existingInstructor));
        when(instructorRepository.save(existingInstructor)).thenReturn(savedInstructor);
        when(instructorMapper.toResponseDto(savedInstructor)).thenReturn(response);

        InstructorResponseDto result = instructorService.updateInstructor(instructorId, dto);

        assertThat(result).isSameAs(response);
        verify(instructorRepository, never()).existsByEmail(dto.getEmail());
        verify(instructorMapper).updateEntityFromDto(dto, existingInstructor);
    }

    @Test
    void updateInstructorThrowsWhenNewEmailAlreadyExists() {
        UUID instructorId = UUID.randomUUID();
        InstructorRequestDto dto = instructorRequest("grace@example.com");
        Instructor existingInstructor = Instructor.builder().id(instructorId).email("ada@example.com").build();

        when(instructorRepository.findById(instructorId)).thenReturn(Optional.of(existingInstructor));
        when(instructorRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> instructorService.updateInstructor(instructorId, dto))
                .isInstanceOf(FunctionalException.class)
                .hasMessage("Instructor with email 'grace@example.com' already exists")
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(instructorRepository, never()).save(any());
    }

    @Test
    void deleteInstructorDeletesExistingInstructor() {
        UUID instructorId = UUID.randomUUID();
        Instructor instructor = Instructor.builder().id(instructorId).build();
        when(instructorRepository.findById(instructorId)).thenReturn(Optional.of(instructor));

        instructorService.deleteInstructor(instructorId);

        verify(instructorRepository).delete(instructor);
    }

    private InstructorRequestDto instructorRequest(String email) {
        return InstructorRequestDto.builder()
                .firstName("Ada")
                .lastName("Lovelace")
                .email(email)
                .department("Computer Science")
                .build();
    }
}
