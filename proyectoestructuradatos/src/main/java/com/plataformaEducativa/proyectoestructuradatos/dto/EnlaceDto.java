// DTO para tipo de contenido ENLACE
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
public class EnlaceDto extends ContenidoDto {

    @NotBlank(message = "La URL no puede estar vacía")
    @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", message = "La URL no tiene un formato válido")
    private String url;

    private String sitioWeb; // Nombre del sitio web (opcional)
}