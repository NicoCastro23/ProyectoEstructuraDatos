package com.plataformaEducativa.proyectoestructuradatos.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.UUID;

import com.plataformaEducativa.proyectoestructuradatos.entity.ColaPrioridadAyudaEntity.NivelUrgencia;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSolicitudAyudaDto {

    @NotBlank(message = "El contenido de la solicitud no puede estar vacío")
    @Size(max = 1000, message = "El contenido no puede exceder los 1000 caracteres")
    private String contenido;

    @NotNull(message = "El ID del estudiante es requerido")
    private UUID estudianteId;

    // Campo opcional para el grupo de estudio
    private UUID grupoEstudioId;

    // Campos para la cola de prioridad
    @NotNull(message = "El nivel de urgencia es requerido")
    private NivelUrgencia nivelUrgencia;

    @NotBlank(message = "El tema de la ayuda es requerido")
    @Size(max = 100, message = "El tema no puede exceder los 100 caracteres")
    private String tema;

    @Size(max = 1000, message = "La descripción no puede exceder los 1000 caracteres")
    private String descripcion;
}
