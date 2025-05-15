package com.plataformaEducativa.proyectoestructuradatos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

import com.plataformaEducativa.proyectoestructuradatos.enums.NivelUrgencia;

/**
 * DTO específico para la creación de nuevas solicitudes en la cola de
 * prioridad.
 * Contiene solo los campos necesarios para la creación, sin incluir IDs
 * generados
 * ni campos calculados.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearSolicitudPrioridadDto {

    @NotBlank(message = "El tema es obligatorio")
    @Size(min = 3, max = 255, message = "El tema debe tener entre 3 y 255 caracteres")
    private String tema;

    @Size(max = 1000, message = "La descripción no puede exceder los 1000 caracteres")
    private String descripcion;

    @NotNull(message = "El nivel de urgencia es obligatorio")
    private NivelUrgencia nivelUrgencia;

    // ID de la solicitud de ayuda relacionada (opcional si se crea directamente)
    private UUID solicitudAyudaId;

    // Datos adicionales si no existe una solicitud de ayuda relacionada
    private UUID estudianteId;
    private String contenidoSolicitud;
}
