package com.plataformaEducativa.proyectoestructuradatos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.plataformaEducativa.proyectoestructuradatos.dto.ColaPrioridadAyudaDto;
import com.plataformaEducativa.proyectoestructuradatos.entity.ColaPrioridadAyudaEntity.NivelUrgencia;
import com.plataformaEducativa.proyectoestructuradatos.service.ColaPrioridadAyudaService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cola-prioridad")
@RequiredArgsConstructor
public class ColaPrioridadAyudaController {

    private final ColaPrioridadAyudaService colaPrioridadAyudaService;

    /**
     * Obtiene todas las solicitudes en cola de prioridad, ordenadas por prioridad
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('MODERADOR')")
    public ResponseEntity<List<ColaPrioridadAyudaDto>> obtenerColaPrioridad() {
        List<ColaPrioridadAyudaDto> cola = colaPrioridadAyudaService.obtenerColaPrioridad();
        return ResponseEntity.ok(cola);
    }

    /**
     * Obtiene la cola de prioridad usando PriorityQueue
     */
    @GetMapping("/queue")
    @PreAuthorize("hasAnyRole('MODERADOR')")
    public ResponseEntity<List<ColaPrioridadAyudaDto>> obtenerColaPrioridadConPriorityQueue() {
        List<ColaPrioridadAyudaDto> cola = colaPrioridadAyudaService.obtenerColaPrioridadConPriorityQueue();
        return ResponseEntity.ok(cola);
    }

    /**
     * Obtiene la siguiente solicitud de mayor prioridad
     */
    @GetMapping("/siguiente")
    @PreAuthorize("hasAnyRole('MODERADOR')")
    public ResponseEntity<ColaPrioridadAyudaDto> obtenerSiguienteSolicitud() {
        ColaPrioridadAyudaDto solicitud = colaPrioridadAyudaService.obtenerSiguienteSolicitud();
        if (solicitud == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(solicitud);
    }

    /**
     * Obtiene solicitudes por nivel de urgencia
     */
    @GetMapping("/nivel/{nivelUrgencia}")
    @PreAuthorize("hasAnyRole('MODERADOR')")
    public ResponseEntity<List<ColaPrioridadAyudaDto>> obtenerSolicitudesPorNivelUrgencia(
            @PathVariable NivelUrgencia nivelUrgencia) {
        List<ColaPrioridadAyudaDto> solicitudes = colaPrioridadAyudaService.obtenerSolicitudesPorNivelUrgencia(nivelUrgencia);
        return ResponseEntity.ok(solicitudes);
    }

    /**
     * Obtiene una solicitud por su ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MODERADOR')")
    public ResponseEntity<ColaPrioridadAyudaDto> obtenerPorId(@PathVariable UUID id) {
        ColaPrioridadAyudaDto solicitud = colaPrioridadAyudaService.obtenerPorId(id);
        return ResponseEntity.ok(solicitud);
    }

    /**
     * Obtiene una solicitud por el ID de la solicitud relacionada
     */
    @GetMapping("/solicitud/{solicitudId}")
    @PreAuthorize("hasAnyRole('MODERADOR')")
    public ResponseEntity<ColaPrioridadAyudaDto> obtenerPorIdSolicitud(@PathVariable UUID solicitudId) {
        ColaPrioridadAyudaDto solicitud = colaPrioridadAyudaService.obtenerPorIdSolicitud(solicitudId);
        return ResponseEntity.ok(solicitud);
    }

    /**
     * Actualiza el nivel de urgencia de una solicitud
     */
    @PutMapping("/{id}/nivel-urgencia")
    @PreAuthorize("hasAnyRole('MODERADOR')")
    public ResponseEntity<ColaPrioridadAyudaDto> actualizarNivelUrgencia(
            @PathVariable UUID id,
            @RequestBody NivelUrgencia nivelUrgencia) {
        ColaPrioridadAyudaDto solicitud = colaPrioridadAyudaService.actualizarNivelUrgencia(id, nivelUrgencia);
        return ResponseEntity.ok(solicitud);
    }

    /**
     * Marca una solicitud como resuelta
     */
    @PutMapping("/{id}/resolver")
    @PreAuthorize("hasAnyRole('MODERADOR')")
    public ResponseEntity<ColaPrioridadAyudaDto> marcarComoResuelta(@PathVariable UUID id) {
        ColaPrioridadAyudaDto solicitud = colaPrioridadAyudaService.marcarComoResuelta(id);
        return ResponseEntity.ok(solicitud);
    }

    /**
     * Busca solicitudes por tema
     */
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('MODERADOR')")
    public ResponseEntity<List<ColaPrioridadAyudaDto>> buscarPorTema(@RequestParam String tema) {
        List<ColaPrioridadAyudaDto> solicitudes = colaPrioridadAyudaService.buscarPorTema(tema);
        return ResponseEntity.ok(solicitudes);
    }
}