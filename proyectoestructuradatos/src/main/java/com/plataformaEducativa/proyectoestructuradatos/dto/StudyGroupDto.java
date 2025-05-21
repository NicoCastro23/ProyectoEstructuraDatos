package com.plataformaEducativa.proyectoestructuradatos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyGroupDto {
    private UUID id;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "At least one topic is required")
    private Set<String> topics = new HashSet<>();

    private Set<UUID> memberIds = new HashSet<>();
    private Set<String> memberUsernames = new HashSet<>();

    private boolean active;
    private Integer maxCapacity;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
