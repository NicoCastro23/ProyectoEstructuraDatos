package com.plataformaEducativa.proyectoestructuradatos.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;

import com.plataformaEducativa.proyectoestructuradatos.dto.ColaPrioridadAyudaDto;
import com.plataformaEducativa.proyectoestructuradatos.entity.ColaPrioridadAyudaEntity;
import com.plataformaEducativa.proyectoestructuradatos.enums.NivelUrgencia;
import com.plataformaEducativa.proyectoestructuradatos.exception.EntityNotFoundException;
import com.plataformaEducativa.proyectoestructuradatos.mapper.PriorityQueueMapper;
import com.plataformaEducativa.proyectoestructuradatos.repository.ColaPrioridadAyudaRepository;
import com.plataformaEducativa.proyectoestructuradatos.models.HelpPriorityQueue;

@Service
@RequiredArgsConstructor
public class ColaPrioridadAyudaService {

    private final ColaPrioridadAyudaRepository colaPrioridadAyudaRepository;
    private final PriorityQueueMapper priorityQueueMapper;

    public List<ColaPrioridadAyudaDto> obtenerColaPrioridad() {
        List<ColaPrioridadAyudaEntity> entidades = colaPrioridadAyudaRepository.findByResueltaFalse();

        List<HelpPriorityQueue> modelos = entidades.stream()
                .map(priorityQueueMapper::toModel)
                .sorted(Comparator.comparingInt(HelpPriorityQueue::calcularPrioridad).reversed())
                .toList();

        return modelos.stream()
                .map(this::convertToDto)
                .toList();
    }

    public ColaPrioridadAyudaDto obtenerSiguienteSolicitud() {
        return obtenerColaPrioridad().stream().findFirst().orElse(null);
    }

    public List<ColaPrioridadAyudaDto> obtenerSolicitudesPorNivelUrgencia(NivelUrgencia nivelUrgencia) {
        return colaPrioridadAyudaRepository.findByNivelUrgenciaAndResueltaFalseOrderByFechaSolicitudAsc(nivelUrgencia)
                .stream()
                .map(priorityQueueMapper::toModel)
                .map(this::convertToDto)
                .toList();
    }

    public ColaPrioridadAyudaDto obtenerPorId(UUID id) {
        ColaPrioridadAyudaEntity entity = colaPrioridadAyudaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No encontrada con ID: " + id));
        return convertToDto(priorityQueueMapper.toModel(entity));
    }

    public ColaPrioridadAyudaDto obtenerPorIdSolicitud(UUID solicitudId) {
        ColaPrioridadAyudaEntity entity = colaPrioridadAyudaRepository.findBySolicitudAyudaId(solicitudId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró solicitud con ID: " + solicitudId));
        return convertToDto(priorityQueueMapper.toModel(entity));
    }

    @Transactional
    public ColaPrioridadAyudaDto actualizarNivelUrgencia(UUID id, NivelUrgencia nuevoNivel) {
        ColaPrioridadAyudaEntity entity = colaPrioridadAyudaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No encontrada con ID: " + id));
        entity.setNivelUrgencia(nuevoNivel);
        return convertToDto(priorityQueueMapper.toModel(colaPrioridadAyudaRepository.save(entity)));
    }

    @Transactional
    public ColaPrioridadAyudaDto marcarComoResuelta(UUID id) {
        ColaPrioridadAyudaEntity entity = colaPrioridadAyudaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No encontrada con ID: " + id));
        entity.setResuelta(true);
        return convertToDto(priorityQueueMapper.toModel(colaPrioridadAyudaRepository.save(entity)));
    }

    public List<ColaPrioridadAyudaDto> buscarPorTema(String tema) {
        return colaPrioridadAyudaRepository.findByTemaContainingIgnoreCase(tema)
                .stream()
                .map(priorityQueueMapper::toModel)
                .map(this::convertToDto)
                .toList();
    }

    private ColaPrioridadAyudaDto convertToDto(HelpPriorityQueue model) {
        ColaPrioridadAyudaDto dto = new ColaPrioridadAyudaDto();
        dto.setId(model.getId());
        dto.setTema(model.getTema());
        dto.setNivelUrgencia(model.getNivelUrgencia());
        dto.setFechaSolicitud(model.getFechaSolicitud());
        dto.setResuelta(model.isResuelta());
        dto.setDescripcion(model.getDescripcion());
        dto.setPrioridad(model.calcularPrioridad());

        // Si tienes solicitud relacionada, inclúyela
        if (model.getSolicitudAyuda() != null) {
            dto.setSolicitudAyudaId(model.getSolicitudAyuda().getId());
            dto.setContenidoSolicitud(model.getSolicitudAyuda().getContenido());
            dto.setEstudianteId(model.getSolicitudAyuda().getEstudiante().getId());
            dto.setNombreEstudiante(model.getSolicitudAyuda().getEstudiante().getName());
        }

        return dto;
    }

    /**
     * Implementación de cola de prioridad usando PriorityQueue de Java
     * Esta es una implementación alternativa que usa la estructura de datos de cola
     * de prioridad
     */
    public List<ColaPrioridadAyudaDto> obtenerColaPrioridadConPriorityQueue() {
        // Obtener entidades desde la BD
        List<ColaPrioridadAyudaEntity> solicitudesNoResueltas = colaPrioridadAyudaRepository.findByResueltaFalse();

        // Convertir a modelos
        List<HelpPriorityQueue> modelos = solicitudesNoResueltas.stream()
                .map(priorityQueueMapper::toModel) // mapper de Entity -> Model
                .toList();

        // Crear una cola de prioridad ordenada por prioridad calculada
        PriorityQueue<HelpPriorityQueue> priorityQueue = new PriorityQueue<>(
                Comparator.comparingInt(HelpPriorityQueue::calcularPrioridad).reversed());

        // Agregar todos los modelos a la cola
        priorityQueue.addAll(modelos);

        // Extraer en orden de prioridad y convertir a DTO
        List<ColaPrioridadAyudaDto> resultado = new ArrayList<>();
        while (!priorityQueue.isEmpty()) {
            HelpPriorityQueue model = priorityQueue.poll();
            resultado.add(convertToDto(model));
        }

        return resultado;
    }

    public ColaPrioridadAyudaDto crearSolicitud(HelpPriorityQueue dto) {
        // Convertir el DTO a la entidad
        ColaPrioridadAyudaEntity entity = new ColaPrioridadAyudaEntity();
        entity.setTema(dto.getTema());
        entity.setDescripcion(dto.getDescripcion());
        entity.setNivelUrgencia(dto.getNivelUrgencia());
        entity.setResuelta(false); // Asumimos que una nueva solicitud no está resuelta
        entity.setFechaSolicitud(LocalDateTime.now());

        // Guardar la entidad en la base de datos
        ColaPrioridadAyudaEntity savedEntity = colaPrioridadAyudaRepository.save(entity);

        // Convertir la entidad guardada de vuelta a DTO
        return new ColaPrioridadAyudaDto(savedEntity);
    }
}
