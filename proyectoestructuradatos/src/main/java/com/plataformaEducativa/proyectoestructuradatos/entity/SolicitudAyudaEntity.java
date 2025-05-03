package com.plataformaEducativa.proyectoestructuradatos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "solicitudes_ayuda")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudAyudaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 1000)
    private String contenido;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private boolean leido;

    // Relación con el estudiante que solicita ayuda
    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    private EstudianteEntity estudiante;

    // Relación con el grupo de estudio si la solicitud está asociada a uno
    @ManyToOne
    @JoinColumn(name = "grupo_estudio_id")
    private GrupoEstudioEntity grupoEstudio;

    // Relación con la cola de prioridad
    @OneToOne(mappedBy = "solicitudAyuda", cascade = CascadeType.ALL)
    private ColaPrioridadAyudaEntity colaPrioridad;

    // Método para responder a una solicitud
    public String responder(String respuesta) {
        return respuesta;
    }
}
