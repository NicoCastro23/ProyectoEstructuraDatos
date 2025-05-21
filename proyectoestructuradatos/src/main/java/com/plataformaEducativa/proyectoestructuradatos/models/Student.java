package com.plataformaEducativa.proyectoestructuradatos.models;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Student extends User {
    private Set<String> academicInterests = new HashSet<>();
    private String fieldOfStudy;
    private String educationLevel;
    private String bio;
    private int connectionCount;
}
