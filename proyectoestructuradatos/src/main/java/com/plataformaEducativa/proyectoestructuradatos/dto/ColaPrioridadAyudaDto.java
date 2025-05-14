package com.plataformaEducativa.proyectoestructuradatos.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

import com.plataformaEducativa.proyectoestructuradatos.entity.ColaPrioridadAyudaEntity;
import com.plataformaEducativa.proyectoestructuradatos.enums.NivelUrgencia;

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

    public ColaPrioridadAyudaDto(ColaPrioridadAyudaEntity entity) {
        this.id = entity.getId();
        this.tema = entity.getTema();
        this.descripcion = entity.getDescripcion();
        this.nivelUrgencia = entity.getNivelUrgencia();
        this.fechaSolicitud = entity.getFechaSolicitud();
        this.resuelta = entity.isResuelta();
    }

}
