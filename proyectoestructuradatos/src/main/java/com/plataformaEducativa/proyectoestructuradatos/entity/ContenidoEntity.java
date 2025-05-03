package com.plataformaEducativa.proyectoestructuradatos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "contenidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContenidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, length = 5000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoContenido tipo;

    @Column(nullable = false)
    private LocalDateTime fechaPublicacion;

    // Relación con el usuario que publicó el contenido
    @ManyToOne
    @JoinColumn(name = "autor_id", nullable = false)
    private UsuarioEntity autor;

    // Relación con las valoraciones que tiene este contenido
    @OneToMany(mappedBy = "contenido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ValoracionEntity> valoraciones = new ArrayList<>();

    // Promedio de valoraciones
    @Column
    private Double promedioValoracion;

    // Enumeración para los tipos de contenido
    public enum TipoContenido {
        ARTICULO,
        VIDEO,
        EJERCICIO,
        QUIZ,
        DOCUMENTO
    }

    // Método para calcular el promedio de valoraciones
    public double calcularPromedioValoracion() {
        if (valoraciones.isEmpty()) {
            return 0.0;
        }

        double suma = 0;
        for (ValoracionEntity valoracion : valoraciones) {
            suma += valoracion.getPuntuacion();
        }

        this.promedioValoracion = suma / valoraciones.size();
        return this.promedioValoracion;
    }

    // Método para agregar una valoración
    public void agregarValoracion(ValoracionEntity valoracion) {
        valoraciones.add(valoracion);
        valoracion.setContenido(this);
        calcularPromedioValoracion();
    }

    // Método para buscar valoración por ID
    public Optional<ValoracionEntity> buscarValoracion(UUID id) {
        return valoraciones.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst();
    }
}
