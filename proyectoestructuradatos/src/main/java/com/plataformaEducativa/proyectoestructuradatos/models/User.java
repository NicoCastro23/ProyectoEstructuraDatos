package com.plataformaEducativa.proyectoestructuradatos.models;

import java.time.LocalDateTime;
import java.util.UUID;

import com.plataformaEducativa.proyectoestructuradatos.enums.UserRole;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@SuperBuilder
public class User {
    private UUID id;
    private String username;
    private String email;
    private String fullName;
    private UserRole role;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
