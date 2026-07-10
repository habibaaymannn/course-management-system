package com.example.course_manag_system.controller;


import com.example.course_manag_system.dto.request.InstructorRequestDto;
import com.example.course_manag_system.dto.response.InstructorResponseDto;
import com.example.course_manag_system.service.InstructorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/instructors")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorService instructorService;

    @PostMapping
    public ResponseEntity<InstructorResponseDto> create(@Valid @RequestBody InstructorRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(instructorService.createInstructor(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstructorResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(instructorService.getInstructorById(id));
    }

    @GetMapping
    public ResponseEntity<Page<InstructorResponseDto>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(instructorService.getAllInstructors(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstructorResponseDto> update(@PathVariable UUID id, @Valid @RequestBody InstructorRequestDto dto) {
        return ResponseEntity.ok(instructorService.updateInstructor(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        instructorService.deleteInstructor(id);
        return ResponseEntity.noContent().build();
    }
}
