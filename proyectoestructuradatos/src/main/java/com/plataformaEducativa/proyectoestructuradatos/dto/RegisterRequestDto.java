package com.plataformaEducativa.proyectoestructuradatos.dto;

import java.util.List;

import com.plataformaEducativa.proyectoestructuradatos.enums.RoleEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDto {
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotNull(message = "El rol es obligatorio")
    private RoleEnum role;

    // Nuevos campos para estudiantes (opcionales)
    private String nivelAcademico;
    private List<String> intereses;
}
