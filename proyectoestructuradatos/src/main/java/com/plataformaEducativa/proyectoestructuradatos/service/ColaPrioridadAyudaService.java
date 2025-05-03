package com.plataformaEducativa.proyectoestructuradatos.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.stream.Collectors;

import com.plataformaEducativa.proyectoestructuradatos.dto.ColaPrioridadAyudaDto;
import com.plataformaEducativa.proyectoestructuradatos.entity.ColaPrioridadAyudaEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.ColaPrioridadAyudaEntity.NivelUrgencia;
import com.plataformaEducativa.proyectoestructuradatos.entity.SolicitudAyudaEntity;
import com.plataformaEducativa.proyectoestructuradatos.exception.EntityNotFoundException;
import com.plataformaEducativa.proyectoestructuradatos.repository.ColaPrioridadAyudaRepository;
import com.plataformaEducativa.proyectoestructuradatos.repository.SolicitudAyudaRepository;

@Service
@RequiredArgsConstructor
public class ColaPrioridadAyudaService {

    private final ColaPrioridadAyudaRepository colaPrioridadAyudaRepository;
    private final SolicitudAyudaRepository solicitudAyudaRepository;

    /**
     * Obtiene todas las solicitudes de ayuda en cola de prioridad, ordenadas por prioridad
     */
    public List<ColaPrioridadAyudaDto> obtenerColaPrioridad() {
        // Obtener todas las solicitudes no resueltas
        List<ColaPrioridadAyudaEntity> solicitudesNoResueltas = colaPrioridadAyudaRepository.findByResueltaFalse();

        // Calcular la prioridad actual para cada una
        for (ColaPrioridadAyudaEntity solicitud : solicitudesNoResueltas) {
            solicitud.calcularPrioridad();
        }

        // Ordenar por prioridad (de mayor a menor)
        solicitudesNoResueltas.sort((s1, s2) -> Integer.compare(s2.calcularPrioridad(), s1.calcularPrioridad()));

        // Convertir a DTOs
        return solicitudesNoResueltas.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Implementación de cola de prioridad usando PriorityQueue de Java
     * Esta es una implementación alternativa que usa la estructura de datos de cola de prioridad
     */
    public List<ColaPrioridadAyudaDto> obtenerColaPrioridadConPriorityQueue() {
        // Obtener todas las solicitudes no resueltas
        List<ColaPrioridadAyudaEntity> solicitudesNoResueltas = colaPrioridadAyudaRepository.findByResueltaFalse();

        // Crear una cola de prioridad (mayor prioridad primero)
        PriorityQueue<ColaPrioridadAyudaEntity> priorityQueue = new PriorityQueue<>(
                Comparator.comparing(ColaPrioridadAyudaEntity::calcularPrioridad).reversed()
        );

        // Añadir todas las solicitudes a la cola
        priorityQueue.addAll(solicitudesNoResueltas);

        // Extraer en orden de prioridad
        List<ColaPrioridadAyudaDto> resultado = new ArrayList<>();
        while (!priorityQueue.isEmpty()) {
            resultado.add(convertToDto(priorityQueue.poll()));
        }

        return resultado;
    }

    /**
     * Obtiene la siguiente solicitud de mayor prioridad
     */
    public ColaPrioridadAyudaDto obtenerSiguienteSolicitud() {
        List<ColaPrioridadAyudaDto> cola = obtenerColaPrioridad();
        if (cola.isEmpty()) {
            return null;
        }
        return cola.get(0); // La primera es la de mayor prioridad
    }

    /**
     * Obtiene solicitudes de ayuda por nivel de urgencia
     */
    public List<ColaPrioridadAyudaDto> obtenerSolicitudesPorNivelUrgencia(NivelUrgencia nivelUrgencia) {
        return colaPrioridadAyudaRepository.findByNivelUrgenciaAndResueltaFalseOrderByFechaSolicitudAsc(nivelUrgencia)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una solicitud de cola por su ID
     */
    public ColaPrioridadAyudaDto obtenerPorId(UUID id) {
        ColaPrioridadAyudaEntity colaPrioridad = colaPrioridadAyudaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud en cola de prioridad no encontrada con ID: " + id));

        return convertToDto(colaPrioridad);
    }

    /**
     * Obtiene una cola de prioridad por el ID de la solicitud relacionada
     */
    public ColaPrioridadAyudaDto obtenerPorIdSolicitud(UUID solicitudId) {
        ColaPrioridadAyudaEntity colaPrioridad = colaPrioridadAyudaRepository.findBySolicitudAyudaId(solicitudId);
        if (colaPrioridad == null) {
            throw new EntityNotFoundException("No se encontró cola de prioridad para la solicitud con ID: " + solicitudId);
        }

        return convertToDto(colaPrioridad);
    }

    /**
     * Actualiza el nivel de urgencia de una solicitud
     */
    @Transactional
    public ColaPrioridadAyudaDto actualizarNivelUrgencia(UUID id, NivelUrgencia nuevoNivel) {
        ColaPrioridadAyudaEntity colaPrioridad = colaPrioridadAyudaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud en cola de prioridad no encontrada con ID: " + id));

        colaPrioridad.setNivelUrgencia(nuevoNivel);
        colaPrioridad = colaPrioridadAyudaRepository.save(colaPrioridad);

        return convertToDto(colaPrioridad);
    }

    /**
     * Marca una solicitud como resuelta
     */
    @Transactional
    public ColaPrioridadAyudaDto marcarComoResuelta(UUID id) {
        ColaPrioridadAyudaEntity colaPrioridad = colaPrioridadAyudaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud en cola de prioridad no encontrada con ID: " + id));

        colaPrioridad.setResuelta(true);
        colaPrioridad = colaPrioridadAyudaRepository.save(colaPrioridad);

        return convertToDto(colaPrioridad);
    }

    /**
     * Busca solicitudes por tema
     */
    public List<ColaPrioridadAyudaDto> buscarPorTema(String tema) {
        return colaPrioridadAyudaRepository.findByTemaContainingIgnoreCase(tema)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad ColaPrioridadAyuda a DTO
     */
    private ColaPrioridadAyudaDto convertToDto(ColaPrioridadAyudaEntity colaPrioridad) {
        ColaPrioridadAyudaDto dto = new ColaPrioridadAyudaDto();
        dto.setId(colaPrioridad.getId());
        dto.setTema(colaPrioridad.getTema());
        dto.setNivelUrgencia(colaPrioridad.getNivelUrgencia());
        dto.setFechaSolicitud(colaPrioridad.getFechaSolicitud());
        dto.setResuelta(colaPrioridad.isResuelta());
        dto.setDescripcion(colaPrioridad.getDescripcion());
        dto.setPrioridad(colaPrioridad.calcularPrioridad());

        // Datos de la solicitud relacionada
        SolicitudAyudaEntity solicitud = colaPrioridad.getSolicitudAyuda();
        if (solicitud != null) {
            dto.setSolicitudAyudaId(solicitud.getId());
            dto.setContenidoSolicitud(solicitud.getContenido());
            dto.setEstudianteId(solicitud.getEstudiante().getId());
            dto.setNombreEstudiante(solicitud.getEstudiante().getName());
        }

        return dto;
    }
}
