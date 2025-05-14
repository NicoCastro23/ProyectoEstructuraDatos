package com.plataformaEducativa.proyectoestructuradatos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

import com.plataformaEducativa.proyectoestructuradatos.enums.NivelUrgencia;

@Entity
@Table(name = "cola_prioridad_ayuda")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
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

}
