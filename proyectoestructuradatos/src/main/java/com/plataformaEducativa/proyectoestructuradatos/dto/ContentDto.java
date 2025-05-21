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

import com.plataformaEducativa.proyectoestructuradatos.enums.ContentType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentDto {
    private UUID id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String contentUrl;

    @NotNull(message = "Content type is required")
    private ContentType contentType;

    private Set<String> tags = new HashSet<>();

    private UUID authorId;
    private String authorUsername;

    private Double averageRating;
    private Integer ratingCount;
    private Integer viewCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}