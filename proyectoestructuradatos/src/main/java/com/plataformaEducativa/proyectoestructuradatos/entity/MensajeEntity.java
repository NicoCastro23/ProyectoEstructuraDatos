package com.plataformaEducativa.proyectoestructuradatos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mensajes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MensajeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 2000)
    private String contenido;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private boolean leido;

    // Relación con el estudiante remitente
    @ManyToOne
    @JoinColumn(name = "remitente_id", nullable = false)
    private UsuarioEntity remitente;

    // Relación con el estudiante destinatario
    @ManyToOne
    @JoinColumn(name = "destinatario_id", nullable = false)
    private UsuarioEntity destinatario;

    // Método para responder a un mensaje
    public MensajeEntity responder(String respuesta) {
        return MensajeEntity.builder()
                .contenido(respuesta)
                .fechaCreacion(LocalDateTime.now())
                .leido(false)
                .remitente(this.destinatario)
                .destinatario(this.remitente)
                .build();
    }
}
