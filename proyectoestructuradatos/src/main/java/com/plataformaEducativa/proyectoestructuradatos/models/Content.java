package com.plataformaEducativa.proyectoestructuradatos.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.plataformaEducativa.proyectoestructuradatos.enums.ContentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Content {
    private UUID id;
    private String title;
    private String description;
    private String contentUrl;
    private ContentType contentType;
    private Set<String> tags = new HashSet<>();
    private Student author;
    private Double averageRating;
    private Integer ratingCount;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
