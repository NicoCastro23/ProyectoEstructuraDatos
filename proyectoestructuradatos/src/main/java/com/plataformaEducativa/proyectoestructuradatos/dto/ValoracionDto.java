package com.plataformaEducativa.proyectoestructuradatos.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValoracionDto {

    private UUID id;

    @NotNull(message = "La puntuación es obligatoria")
    @Min(value = 1, message = "La puntuación mínima es 1")
    @Max(value = 5, message = "La puntuación máxima es 5")
    private Integer puntuacion;

    @Size(max = 1000, message = "El comentario no puede exceder los 1000 caracteres")
    private String comentario;

    private LocalDate fecha;

    @NotNull(message = "El ID del contenido es obligatorio")
    private UUID contenidoId;

    @NotNull(message = "El ID del estudiante es obligatorio")
    private UUID estudianteId;
}

