package com.plataformaEducativa.proyectoestructuradatos.mapper;

import com.plataformaEducativa.proyectoestructuradatos.dto.RegisterRequestDto;
import com.plataformaEducativa.proyectoestructuradatos.entity.EstudianteEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.ModeradorEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.UsuarioEntity;
import com.plataformaEducativa.proyectoestructuradatos.enums.RoleEnum;
import com.plataformaEducativa.proyectoestructuradatos.models.Usuario;

import org.springframework.security.crypto.password.PasswordEncoder;

public class UsuarioMapper {

    /**
     * Convierte una entidad de usuario a un modelo de usuario
     * 
     * @param entity Entidad de usuario
     * @return Modelo de usuario
     */
    public static Usuario toModel(UsuarioEntity entity) {
        if (entity == null) {
            return null;
        }

        return Usuario.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .name(entity.getName())
                .role(entity.getRole())
                .build(); // No incluimos la contraseña por seguridad
    }

    /**
     * Convierte un DTO de registro a una entidad de estudiante
     * 
     * @param dto             DTO de registro
     * @param passwordEncoder Codificador de contraseñas
     * @return Entidad de estudiante
     */
    public static EstudianteEntity toEstudianteEntity(RegisterRequestDto dto, PasswordEncoder passwordEncoder) {
        if (dto == null) {
            return null;
        }

        EstudianteEntity entity = new EstudianteEntity();
        entity.setEmail(dto.getEmail());
        entity.setName(dto.getName());
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.setRole(RoleEnum.ESTUDIANTE);

        // Establecer los nuevos campos si están presentes
        if (dto.getNivelAcademico() != null) {
            entity.setNivelAcademico(dto.getNivelAcademico());
        }
        if (dto.getIntereses() != null) {
            entity.setIntereses(dto.getIntereses());
        }

        return entity;
    }

    /**
     * Convierte un DTO de registro a una entidad de moderador
     * 
     * @param dto             DTO de registro
     * @param passwordEncoder Codificador de contraseñas
     * @return Entidad de moderador
     */
    public static ModeradorEntity toModeradorEntity(RegisterRequestDto dto, PasswordEncoder passwordEncoder) {
        if (dto == null) {
            return null;
        }

        ModeradorEntity entity = new ModeradorEntity();
        entity.setEmail(dto.getEmail());
        entity.setName(dto.getName());
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.setRole(RoleEnum.MODERADOR);

        return entity;
    }
}