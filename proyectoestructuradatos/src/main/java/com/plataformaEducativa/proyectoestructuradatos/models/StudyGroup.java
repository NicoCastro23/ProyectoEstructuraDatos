package com.plataformaEducativa.proyectoestructuradatos.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyGroup {
    private UUID id;
    private String name;
    private String description;
    private Set<String> topics = new HashSet<>();
    private Set<Student> members = new HashSet<>();
    private boolean active;
    private Integer maxCapacity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
