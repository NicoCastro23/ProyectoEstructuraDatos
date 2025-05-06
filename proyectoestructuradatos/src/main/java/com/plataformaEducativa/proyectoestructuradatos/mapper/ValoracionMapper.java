package com.plataformaEducativa.proyectoestructuradatos.mapper;

import com.plataformaEducativa.proyectoestructuradatos.dto.CrearValoracionDto;
import com.plataformaEducativa.proyectoestructuradatos.dto.ValoracionDto;
import com.plataformaEducativa.proyectoestructuradatos.entity.ContenidoEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.EstudianteEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.ValoracionEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ValoracionMapper {

    /**
     * Convierte una entidad de valoración a su correspondiente DTO
     */
    public ValoracionDto toDTO(ValoracionEntity entity) {
        return ValoracionDto.builder()
                .id(entity.getId())
                .puntuacion(entity.getPuntuacion())
                .comentario(entity.getComentario())
                .fecha(entity.getFecha())
                .contenidoId(entity.getContenido().getId())
                .estudianteId(entity.getEstudiante().getId())
                .build();
    }

    /**
     * Convierte un DTO de creación de valoración a una entidad de valoración
     */
    public ValoracionEntity fromCrearValoracionDTO(CrearValoracionDto dto, ContenidoEntity contenido, EstudianteEntity estudiante) {
        return ValoracionEntity.builder()
                .puntuacion(dto.getPuntuacion())
                .comentario(dto.getComentario())
                .fecha(LocalDate.now())
                .contenido(contenido)
                .estudiante(estudiante)
                .build();
    }
}