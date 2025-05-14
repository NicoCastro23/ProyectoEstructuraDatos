package com.plataformaEducativa.proyectoestructuradatos.models;

import java.time.LocalDate;
import java.util.UUID;

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
public class Value {
    private UUID id;
    private int puntuacion; // Por ejemplo, de 1 a 5
    private String comentario;
    private LocalDate fecha;
    private Content content;
    private Estudiante estudiante;

}
