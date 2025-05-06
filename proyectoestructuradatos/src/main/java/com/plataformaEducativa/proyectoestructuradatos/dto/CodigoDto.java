// DTO para tipo de contenido CODIGO
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
public class CodigoDto extends ContenidoDto {

    @NotBlank(message = "El código no puede estar vacío")
    private String codigo;

    @NotBlank(message = "El lenguaje de programación no puede estar vacío")
    private String lenguaje; // Java, Python, etc.
}
