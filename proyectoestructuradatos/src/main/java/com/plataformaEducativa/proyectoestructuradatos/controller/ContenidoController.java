package com.plataformaEducativa.proyectoestructuradatos.controller;

import com.plataformaEducativa.proyectoestructuradatos.dto.*;
import com.plataformaEducativa.proyectoestructuradatos.enums.TipoContenido;
import com.plataformaEducativa.proyectoestructuradatos.service.ContenidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contenidos")
@RequiredArgsConstructor
public class ContenidoController {

    private final ContenidoService contenidoService;

    /**
     * Endpoint para obtener todos los contenidos
     */
    @GetMapping
    public ResponseEntity<List<ContenidoDto>> obtenerTodosLosContenidos() {
        return ResponseEntity.ok(contenidoService.obtenerTodosLosContenidos());
    }

    /**
     * Endpoint para obtener un contenido por su ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContenidoDto> obtenerContenidoPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(contenidoService.obtenerContenidoPorId(id));
    }

    /**
     * Endpoint para obtener contenidos por tipo
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<ContenidoDto>> obtenerContenidosPorTipo(@PathVariable TipoContenido tipo) {
        return ResponseEntity.ok(contenidoService.obtenerContenidosPorTipo(tipo));
    }

    /**
     * Endpoint para obtener contenidos por autor
     */
    @GetMapping("/autor/{autorId}")
    public ResponseEntity<List<ContenidoDto>> obtenerContenidosPorAutor(@PathVariable UUID autorId) {
        return ResponseEntity.ok(contenidoService.obtenerContenidosPorAutor(autorId));
    }

    /**
     * Endpoint para crear un documento
     */
    @PostMapping("/documentos")
    public ResponseEntity<ContenidoDto> crearDocumento(
            @Valid @RequestBody DocumentoDto documentoDTO,
            @RequestHeader("Usuario-Id") UUID usuarioId) {
        return new ResponseEntity<>(contenidoService.crearDocumento(documentoDTO, usuarioId), HttpStatus.CREATED);
    }

    /**
     * Endpoint para crear un enlace
     */
    @PostMapping("/enlaces")
    public ResponseEntity<ContenidoDto> crearEnlace(
            @Valid @RequestBody EnlaceDto enlaceDTO,
            @RequestHeader("Usuario-Id") UUID usuarioId) {
        return new ResponseEntity<>(contenidoService.crearEnlace(enlaceDTO, usuarioId), HttpStatus.CREATED);
    }

    /**
     * Endpoint para crear un video
     */
    @PostMapping("/videos")
    public ResponseEntity<ContenidoDto> crearVideo(
            @Valid @RequestBody VideoDto videoDTO,
            @RequestHeader("Usuario-Id") UUID usuarioId) {
        return new ResponseEntity<>(contenidoService.crearVideo(videoDTO, usuarioId), HttpStatus.CREATED);
    }

    /**
     * Endpoint para crear una imagen
     */
    @PostMapping("/imagenes")
    public ResponseEntity<ContenidoDto> crearImagen(
            @Valid @RequestBody ImagenDto imagenDTO,
            @RequestHeader("Usuario-Id") UUID usuarioId) {
        return new ResponseEntity<>(contenidoService.crearImagen(imagenDTO, usuarioId), HttpStatus.CREATED);
    }

    /**
     * Endpoint para crear código
     */
    @PostMapping("/codigos")
    public ResponseEntity<ContenidoDto> crearCodigo(
            @Valid @RequestBody CodigoDto codigoDTO,
            @RequestHeader("Usuario-Id") UUID usuarioId) {
        return new ResponseEntity<>(contenidoService.crearCodigo(codigoDTO, usuarioId), HttpStatus.CREATED);
    }

    /**
     * Endpoint para eliminar un contenido
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarContenido(@PathVariable UUID id) {
        contenidoService.eliminarContenido(id);
        return ResponseEntity.noContent().build();
    }
}
