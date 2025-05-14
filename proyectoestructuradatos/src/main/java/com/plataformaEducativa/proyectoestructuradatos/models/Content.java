package com.plataformaEducativa.proyectoestructuradatos.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.plataformaEducativa.proyectoestructuradatos.enums.TipoContenido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Content {
    private UUID id;
    private String titulo;
    private String descripcion;
    private TipoContenido tipo;
    private LocalDateTime fechaPublicacion;
    private Usuario autor;
    private List<Value> valoraciones = new ArrayList<>();
    private Double promedioValoracion;

    // Método para calcular el promedio de valoraciones
    public double calcularPromedioValoracion() {
        if (valoraciones.isEmpty()) {
            return 0.0;
        }

        double suma = 0;
        for (Value valoracion : valoraciones) {
            suma += valoracion.getPuntuacion();
        }

        this.promedioValoracion = suma / valoraciones.size();
        return this.promedioValoracion;
    }

    // Método para agregar una valoración
    public void agregarValoracion(Value valoracion) {
        valoraciones.add(valoracion);
        valoracion.setContent(this);
        calcularPromedioValoracion();
    }

    // Método para buscar valoración por ID
    public Optional<Value> buscarValoracion(UUID id) {
        return valoraciones.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst();
    }
}
