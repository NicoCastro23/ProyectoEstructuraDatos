// DTO para tipo de contenido VIDEO
package com.plataformaEducativa.proyectoestructuradatos.dto;

import jakarta.validation.constraints.Min;
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
public class VideoDto extends ContenidoDto {

    @NotBlank(message = "La URL del video no puede estar vacía")
    @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", message = "La URL no tiene un formato válido")
    private String url;

    @Min(value = 1, message = "La duración debe ser mayor a 0")
    private Integer duracionSegundos;

    private String plataforma; // YouTube, Vimeo, etc.
}
