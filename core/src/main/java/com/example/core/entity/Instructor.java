package com.example.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "instructors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Instructor extends User{

    private String department;

    @Builder.Default
    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Course> courses = new ArrayList<>();
}
