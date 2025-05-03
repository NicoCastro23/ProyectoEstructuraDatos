package com.plataformaEducativa.proyectoestructuradatos.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

import com.plataformaEducativa.proyectoestructuradatos.entity.ColaPrioridadAyudaEntity.NivelUrgencia;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColaPrioridadAyudaDto {

    private UUID id;
    private String tema;
    private NivelUrgencia nivelUrgencia;
    private LocalDateTime fechaSolicitud;
    private boolean resuelta;
    private String descripcion;

    // Datos simplificados de la solicitud relacionada
    private UUID solicitudAyudaId;
    private String contenidoSolicitud;
    private String nombreEstudiante;
    private UUID estudianteId;

    // Campo calculado para mostrar la prioridad
    private int prioridad;
}
