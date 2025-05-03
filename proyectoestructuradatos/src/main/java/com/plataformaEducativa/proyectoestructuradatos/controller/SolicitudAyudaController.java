package com.plataformaEducativa.proyectoestructuradatos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.plataformaEducativa.proyectoestructuradatos.dto.CreateSolicitudAyudaDto;
import com.plataformaEducativa.proyectoestructuradatos.dto.SolicitudAyudaDto;
import com.plataformaEducativa.proyectoestructuradatos.service.SolicitudAyudaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/solicitudes-ayuda")
@RequiredArgsConstructor
public class SolicitudAyudaController {

    private final SolicitudAyudaService solicitudAyudaService;

    /**
     * Crea una nueva solicitud de ayuda
     */
    @PostMapping
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<SolicitudAyudaDto> crearSolicitudAyuda(@Valid @RequestBody CreateSolicitudAyudaDto createDto) {
        SolicitudAyudaDto nuevaSolicitud = solicitudAyudaService.crearSolicitudAyuda(createDto);
        return new ResponseEntity<>(nuevaSolicitud, HttpStatus.CREATED);
    }

    /**
     * Obtiene todas las solicitudes de ayuda
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('MODERADOR', 'ESTUDIANTE')")
    public ResponseEntity<List<SolicitudAyudaDto>> obtenerTodasLasSolicitudes() {
        List<SolicitudAyudaDto> solicitudes = solicitudAyudaService.obtenerTodasLasSolicitudes();
        return ResponseEntity.ok(solicitudes);
    }

    /**
     * Obtiene todas las solicitudes no resueltas
     */
    @GetMapping("/no-resueltas")
    @PreAuthorize("hasAnyRole('MODERADOR', 'ESTUDIANTE')")
    public ResponseEntity<List<SolicitudAyudaDto>> obtenerSolicitudesNoResueltas() {
        List<SolicitudAyudaDto> solicitudes = solicitudAyudaService.obtenerSolicitudesNoResueltas();
        return ResponseEntity.ok(solicitudes);
    }

    /**
     * Obtiene una solicitud por su ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MODERADOR', 'ESTUDIANTE')")
    public ResponseEntity<SolicitudAyudaDto> obtenerSolicitudPorId(@PathVariable UUID id) {
        SolicitudAyudaDto solicitud = solicitudAyudaService.obtenerSolicitudPorId(id);
        return ResponseEntity.ok(solicitud);
    }

    /**
     * Obtiene solicitudes por estudiante
     */
    @GetMapping("/estudiante/{estudianteId}")
    @PreAuthorize("hasAnyRole('MODERADOR') or #estudianteId == authentication.principal.id")
    public ResponseEntity<List<SolicitudAyudaDto>> obtenerSolicitudesPorEstudiante(@PathVariable UUID estudianteId) {
        List<SolicitudAyudaDto> solicitudes = solicitudAyudaService.obtenerSolicitudesPorEstudiante(estudianteId);
        return ResponseEntity.ok(solicitudes);
    }

    /**
     * Obtiene solicitudes por grupo
     */
    @GetMapping("/grupo/{grupoId}")
    @PreAuthorize("hasAnyRole('MODERADOR', 'ESTUDIANTE')")
    public ResponseEntity<List<SolicitudAyudaDto>> obtenerSolicitudesPorGrupo(@PathVariable UUID grupoId) {
        List<SolicitudAyudaDto> solicitudes = solicitudAyudaService.obtenerSolicitudesPorGrupo(grupoId);
        return ResponseEntity.ok(solicitudes);
    }

    /**
     * Marca una solicitud como leída
     */
    @PutMapping("/{id}/leer")
    @PreAuthorize("hasAnyRole('MODERADOR', 'ESTUDIANTE')")
    public ResponseEntity<SolicitudAyudaDto> marcarComoLeida(@PathVariable UUID id) {
        SolicitudAyudaDto solicitud = solicitudAyudaService.marcarComoLeida(id);
        return ResponseEntity.ok(solicitud);
    }

    /**
     * Marca una solicitud como resuelta
     */
    @PutMapping("/{id}/resolver")
    @PreAuthorize("hasAnyRole('MODERADOR')")
    public ResponseEntity<SolicitudAyudaDto> marcarComoResuelta(@PathVariable UUID id) {
        SolicitudAyudaDto solicitud = solicitudAyudaService.marcarComoResuelta(id);
        return ResponseEntity.ok(solicitud);
    }

    /**
     * Responde a una solicitud
     */
    @PostMapping("/{id}/responder")
    @PreAuthorize("hasAnyRole('MODERADOR', 'ESTUDIANTE')")
    public ResponseEntity<String> responderSolicitud(@PathVariable UUID id, @RequestBody String respuesta) {
        String mensajeRespuesta = solicitudAyudaService.responderSolicitud(id, respuesta);
        return ResponseEntity.ok(mensajeRespuesta);
    }

    /**
     * Elimina una solicitud
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MODERADOR')")
    public ResponseEntity<Void> eliminarSolicitud(@PathVariable UUID id) {
        solicitudAyudaService.eliminarSolicitud(id);
        return ResponseEntity.noContent().build();
    }
}
