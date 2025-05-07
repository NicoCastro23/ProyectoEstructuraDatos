package com.plataformaEducativa.proyectoestructuradatos.controller;

import com.plataformaEducativa.proyectoestructuradatos.dto.CrearValoracionDto;
import com.plataformaEducativa.proyectoestructuradatos.dto.ValoracionDto;
import com.plataformaEducativa.proyectoestructuradatos.service.ValoracionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/valoraciones")
@RequiredArgsConstructor
public class ValoracionController {

    private final ValoracionService valoracionService;

    /**
     * Endpoint para crear una nueva valoración
     */
    @PostMapping
    public ResponseEntity<ValoracionDto> crearValoracion(
            @Valid @RequestBody CrearValoracionDto crearValoracionDTO,
            @RequestHeader("Usuario-Id") UUID usuarioId) {
        return new ResponseEntity<>(valoracionService.crearValoracion(crearValoracionDTO, usuarioId), HttpStatus.CREATED);
    }

    /**
     * Endpoint para obtener todas las valoraciones de un contenido
     */
    @GetMapping("/contenido/{contenidoId}")
    public ResponseEntity<List<ValoracionDto>> obtenerValoracionesPorContenido(@PathVariable UUID contenidoId) {
        return ResponseEntity.ok(valoracionService.obtenerValoracionesPorContenido(contenidoId));
    }

    /**
     * Endpoint para obtener todas las valoraciones hechas por un estudiante
     */
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<ValoracionDto>> obtenerValoracionesPorEstudiante(@PathVariable UUID estudianteId) {
        return ResponseEntity.ok(valoracionService.obtenerValoracionesPorEstudiante(estudianteId));
    }

    /**
     * Endpoint para eliminar una valoración
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarValoracion(
            @PathVariable UUID id,
            @RequestHeader("Usuario-Id") UUID usuarioId) {
        valoracionService.eliminarValoracion(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}