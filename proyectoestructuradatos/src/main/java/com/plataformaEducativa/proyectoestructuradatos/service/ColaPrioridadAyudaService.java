package com.plataformaEducativa.proyectoestructuradatos.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plataformaEducativa.proyectoestructuradatos.dto.ColaPrioridadAyudaDto;
import com.plataformaEducativa.proyectoestructuradatos.dto.CrearSolicitudPrioridadDto;
import com.plataformaEducativa.proyectoestructuradatos.entity.ColaPrioridadAyudaEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.EstudianteEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.SolicitudAyudaEntity;
import com.plataformaEducativa.proyectoestructuradatos.enums.NivelUrgencia;
import com.plataformaEducativa.proyectoestructuradatos.exception.EntityNotFoundException;
import com.plataformaEducativa.proyectoestructuradatos.mapper.PriorityQueueMapper;
import com.plataformaEducativa.proyectoestructuradatos.models.HelpPriorityQueue;
import com.plataformaEducativa.proyectoestructuradatos.repository.ColaPrioridadAyudaRepository;
import com.plataformaEducativa.proyectoestructuradatos.repository.EstudianteRepository;
import com.plataformaEducativa.proyectoestructuradatos.repository.SolicitudAyudaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ColaPrioridadAyudaService {

    private final ColaPrioridadAyudaRepository colaPrioridadAyudaRepository;
    private final SolicitudAyudaRepository solicitudAyudaRepository;
    private final EstudianteRepository estudianteRepository;
    private final PriorityQueueMapper priorityQueueMapper;

    @Transactional
    public ColaPrioridadAyudaDto crearSolicitudPrioridad(CrearSolicitudPrioridadDto solicitudDto) {
        ColaPrioridadAyudaEntity nuevaSolicitud = new ColaPrioridadAyudaEntity();
        nuevaSolicitud.setTema(solicitudDto.getTema());
        nuevaSolicitud.setDescripcion(solicitudDto.getDescripcion());
        nuevaSolicitud.setNivelUrgencia(solicitudDto.getNivelUrgencia());
        nuevaSolicitud.setFechaSolicitud(LocalDateTime.now());
        nuevaSolicitud.setResuelta(false);

        // Si se proporciona un ID de solicitud de ayuda, buscarla y asociarla
        if (solicitudDto.getSolicitudAyudaId() != null) {
            SolicitudAyudaEntity solicitudAyuda = solicitudAyudaRepository.findById(solicitudDto.getSolicitudAyudaId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Solicitud de ayuda no encontrada con ID: " + solicitudDto.getSolicitudAyudaId()));
            nuevaSolicitud.setSolicitudAyuda(solicitudAyuda);
        }
        // Si no hay solicitud de ayuda pero se proporcionan datos para crearla
        else if (solicitudDto.getEstudianteId() != null && solicitudDto.getContenidoSolicitud() != null) {
            EstudianteEntity estudiante = estudianteRepository.findById(solicitudDto.getEstudianteId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Estudiante no encontrado con ID: " + solicitudDto.getEstudianteId()));

            // Crear nueva solicitud de ayuda
            SolicitudAyudaEntity nuevaSolicitudAyuda = new SolicitudAyudaEntity();
            nuevaSolicitudAyuda.setEstudiante(estudiante);
            nuevaSolicitudAyuda.setContenido(solicitudDto.getContenidoSolicitud());
            nuevaSolicitudAyuda.setFechaCreacion(LocalDateTime.now());

            // Guardar la nueva solicitud de ayuda
            SolicitudAyudaEntity solicitudGuardada = solicitudAyudaRepository.save(nuevaSolicitudAyuda);
            nuevaSolicitud.setSolicitudAyuda(solicitudGuardada);
        }

        // Guardar la solicitud en la cola de prioridad
        ColaPrioridadAyudaEntity solicitudGuardada = colaPrioridadAyudaRepository.save(nuevaSolicitud);
        log.info("Nueva solicitud creada en cola de prioridad con ID: {}", solicitudGuardada.getId());

        // Convertir a modelo y luego a DTO para devolverlo
        HelpPriorityQueue modelo = priorityQueueMapper.toModel(solicitudGuardada);
        return convertToDto(modelo);
    }

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
        log.info("Nivel de urgencia actualizado para solicitud ID: {}, nuevo nivel: {}", id, nuevoNivel);
        return convertToDto(priorityQueueMapper.toModel(colaPrioridadAyudaRepository.save(entity)));
    }

    @Transactional
    public ColaPrioridadAyudaDto marcarComoResuelta(UUID id) {
        ColaPrioridadAyudaEntity entity = colaPrioridadAyudaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No encontrada con ID: " + id));
        entity.setResuelta(true);
        log.info("Solicitud marcada como resuelta con ID: {}", id);
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
            if (model.getSolicitudAyuda().getEstudiante() != null) {
                dto.setEstudianteId(model.getSolicitudAyuda().getEstudiante().getId());
                dto.setNombreEstudiante(model.getSolicitudAyuda().getEstudiante().getName());
            }
        }

        return dto;
    }

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
}
