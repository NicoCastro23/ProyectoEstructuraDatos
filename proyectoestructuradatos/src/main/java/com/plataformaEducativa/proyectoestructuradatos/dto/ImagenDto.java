package com.plataformaEducativa.proyectoestructuradatos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class ImagenDto extends ContenidoDto {

    @NotBlank(message = "La URL o ruta de la imagen no puede estar vacía")
    private String rutaImagen;

    private String formatoImagen; // PNG, JPG, etc.

    private Integer ancho;

    private Integer alto;
}