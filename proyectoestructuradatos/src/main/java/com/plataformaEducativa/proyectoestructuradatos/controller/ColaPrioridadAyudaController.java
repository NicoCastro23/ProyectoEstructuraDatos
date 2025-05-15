package com.plataformaEducativa.proyectoestructuradatos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.plataformaEducativa.proyectoestructuradatos.dto.ColaPrioridadAyudaDto;
import com.plataformaEducativa.proyectoestructuradatos.dto.CrearSolicitudPrioridadDto;
import com.plataformaEducativa.proyectoestructuradatos.enums.NivelUrgencia;
import com.plataformaEducativa.proyectoestructuradatos.service.ColaPrioridadAyudaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/cola-prioridad")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MODERADOR')")
public class ColaPrioridadAyudaController {

    private final ColaPrioridadAyudaService colaPrioridadAyudaService;

    @GetMapping
    public ResponseEntity<List<ColaPrioridadAyudaDto>> listarTodas() {
        return ResponseEntity.ok(colaPrioridadAyudaService.obtenerColaPrioridad());
    }

    @GetMapping("/queue")
    public ResponseEntity<List<ColaPrioridadAyudaDto>> listarConPriorityQueue() {
        return ResponseEntity.ok(colaPrioridadAyudaService.obtenerColaPrioridadConPriorityQueue());
    }

    @GetMapping("/siguiente")
    public ResponseEntity<ColaPrioridadAyudaDto> obtenerSiguiente() {
        return ResponseEntity.of(Optional.ofNullable(colaPrioridadAyudaService.obtenerSiguienteSolicitud()));
    }

    @GetMapping("/nivel/{nivelUrgencia}")
    public ResponseEntity<List<ColaPrioridadAyudaDto>> listarPorNivelUrgencia(
            @PathVariable NivelUrgencia nivelUrgencia) {
        return ResponseEntity.ok(colaPrioridadAyudaService.obtenerSolicitudesPorNivelUrgencia(nivelUrgencia));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColaPrioridadAyudaDto> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(colaPrioridadAyudaService.obtenerPorId(id));
    }

    @GetMapping("/solicitud/{solicitudId}")
    public ResponseEntity<ColaPrioridadAyudaDto> obtenerPorSolicitudId(@PathVariable UUID solicitudId) {
        return ResponseEntity.ok(colaPrioridadAyudaService.obtenerPorIdSolicitud(solicitudId));
    }

    @PutMapping("/{id}/nivel-urgencia")
    public ResponseEntity<ColaPrioridadAyudaDto> actualizarNivelUrgencia(
            @PathVariable UUID id,
            @RequestBody NivelUrgencia nivelUrgencia) {
        return ResponseEntity.ok(colaPrioridadAyudaService.actualizarNivelUrgencia(id, nivelUrgencia));
    }

    @PutMapping("/{id}/resolver")
    public ResponseEntity<ColaPrioridadAyudaDto> marcarComoResuelta(@PathVariable UUID id) {
        return ResponseEntity.ok(colaPrioridadAyudaService.marcarComoResuelta(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ColaPrioridadAyudaDto>> buscarPorTema(@RequestParam String tema) {
        return ResponseEntity.ok(colaPrioridadAyudaService.buscarPorTema(tema));

    }

    /*
     * Crea una nueva solicitud en la cola de prioridad
     * 
     * @param solicitudDto datos de la nueva solicitud
     * 
     * @return la solicitud creada con su información completa
     */
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ColaPrioridadAyudaDto> crearSolicitudPrioridad(
            @Valid @RequestBody CrearSolicitudPrioridadDto solicitudDto) {
        ColaPrioridadAyudaDto nuevaSolicitud = colaPrioridadAyudaService.crearSolicitudPrioridad(solicitudDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaSolicitud);
    }
}
