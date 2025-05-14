// DTO para tipo de contenido DOCUMENTO
package com.plataformaEducativa.proyectoestructuradatos.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class DocumentoDto extends ContenidoDto {

    @NotBlank(message = "La URL o ruta del documento no puede estar vacía")
    private String rutaArchivo;

    @NotBlank(message = "El formato del documento no puede estar vacío")
    private String formato; // PDF, DOC, etc.

    private Long tamanoBytes;
}
