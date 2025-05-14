package com.plataformaEducativa.proyectoestructuradatos.models;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.plataformaEducativa.proyectoestructuradatos.entity.SolicitudAyudaEntity;
import com.plataformaEducativa.proyectoestructuradatos.enums.NivelUrgencia;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class HelpPriorityQueue {
    private UUID id;
    private String tema;
    private NivelUrgencia nivelUrgencia;
    private LocalDateTime fechaSolicitud;
    private boolean resuelta;
    private String descripcion;
    private SolicitudAyudaEntity solicitudAyuda;

    public int calcularPrioridad() {
        int basePrioridad;
        switch (nivelUrgencia) {
            case BAJA:
                basePrioridad = 1;
                break;
            case MEDIA:
                basePrioridad = 3;
                break;
            case ALTA:
                basePrioridad = 6;
                break;
            case CRITICA:
                basePrioridad = 10;
                break;
            default:
                basePrioridad = 1;
        }

        long horasEspera = java.time.Duration.between(fechaSolicitud, LocalDateTime.now()).toHours();
        int factorTiempo = (int) Math.min(10, horasEspera / 2);

        return basePrioridad + factorTiempo;
    }
}
