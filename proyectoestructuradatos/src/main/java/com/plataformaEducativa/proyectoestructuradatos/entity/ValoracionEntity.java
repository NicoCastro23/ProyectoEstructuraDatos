package com.plataformaEducativa.proyectoestructuradatos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "valoraciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValoracionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private int puntuacion; // Por ejemplo, de 1 a 5

    @Column(length = 1000)
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime fecha;

    // Relación con el contenido valorado
    @ManyToOne
    @JoinColumn(name = "contenido_id", nullable = false)
    private ContenidoEntity contenido;

    // Relación con el usuario que hizo la valoración
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;
}
