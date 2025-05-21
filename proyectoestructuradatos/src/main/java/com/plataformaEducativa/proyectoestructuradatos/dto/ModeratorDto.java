package com.plataformaEducativa.proyectoestructuradatos.dto;

import com.plataformaEducativa.proyectoestructuradatos.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO simplificado para Moderadores que evita problemas de herencia
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModeratorDto {
    // Propiedades de UserDto
    private UUID id;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotNull(message = "Role is required")
    private UserRole role;

    private boolean active;

    // Propiedades específicas de ModeratorDto
    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotNull(message = "Access level is required")
    private Integer accessLevel;

    private String contactInfo;
}
