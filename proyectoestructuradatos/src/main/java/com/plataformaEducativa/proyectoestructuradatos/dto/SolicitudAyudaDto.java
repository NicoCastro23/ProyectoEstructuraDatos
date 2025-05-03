package com.plataformaEducativa.proyectoestructuradatos.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

import com.plataformaEducativa.proyectoestructuradatos.entity.ColaPrioridadAyudaEntity.NivelUrgencia;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudAyudaDto {

    private UUID id;
    private String contenido;
    private LocalDateTime fechaCreacion;
    private boolean leido;
    private UUID estudianteId;
    private String estudianteNombre;
    private UUID grupoEstudioId;
    private String grupoEstudioNombre;

    // Campos para la cola de prioridad
    private NivelUrgencia nivelUrgencia;
    private String tema;
    private String descripcion;
    private boolean resuelta;
}
