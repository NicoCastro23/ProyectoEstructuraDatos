package com.plataformaEducativa.proyectoestructuradatos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cola_prioridad_ayuda")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColaPrioridadAyudaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String tema;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelUrgencia nivelUrgencia;

    @Column(nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column(nullable = false)
    private boolean resuelta;

    @Column(length = 1000)
    private String descripcion;

    // Relación con la solicitud de ayuda
    @OneToOne
    @JoinColumn(name = "solicitud_ayuda_id")
    private SolicitudAyudaEntity solicitudAyuda;

    // Enumeración interna para niveles de urgencia
    public enum NivelUrgencia {
        BAJA,
        MEDIA,
        ALTA,
        CRITICA
    }

    // Método para calcular prioridad basado en el nivel de urgencia y tiempo de espera
    public int calcularPrioridad() {
        // La base de prioridad depende del nivel de urgencia
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

        // Añadir factor de tiempo (mayor tiempo de espera = mayor prioridad)
        long horasEspera = java.time.Duration.between(fechaSolicitud, LocalDateTime.now()).toHours();
        int factorTiempo = (int) Math.min(10, horasEspera / 2); // Máximo 10 puntos por tiempo

        return basePrioridad + factorTiempo;
    }
}
