package com.plataformaEducativa.proyectoestructuradatos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import com.plataformaEducativa.proyectoestructuradatos.enums.HelpRequestPriority;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HelpRequestDto {
    private UUID id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Topic is required")
    private String topic;

    @NotNull(message = "Priority is required")
    private HelpRequestPriority priority;

    private UUID requesterId;
    private String requesterUsername;

    private UUID helperId;
    private String helperUsername;

    private boolean resolved;
    private LocalDateTime resolvedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
