// Base DTO para todos los tipos de contenido
package com.plataformaEducativa.proyectoestructuradatos.dto;

import com.plataformaEducativa.proyectoestructuradatos.enums.TipoContenido;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class ContenidoDto {

    private UUID id;

    @NotBlank(message = "El título no puede estar vacío")
    @Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres")
    private String titulo;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(min = 10, max = 5000, message = "La descripción debe tener entre 10 y 5000 caracteres")
    private String descripcion;

    @NotNull(message = "El tipo de contenido es obligatorio")
    private TipoContenido tipo;

    @NotNull(message = "El ID del autor es obligatorio")
    private UUID autorId;

    private Double promedioValoracion;
}
