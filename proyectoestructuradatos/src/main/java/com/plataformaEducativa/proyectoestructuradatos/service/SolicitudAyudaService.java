package com.plataformaEducativa.proyectoestructuradatos.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.plataformaEducativa.proyectoestructuradatos.dto.CreateSolicitudAyudaDto;
import com.plataformaEducativa.proyectoestructuradatos.dto.SolicitudAyudaDto;
import com.plataformaEducativa.proyectoestructuradatos.entity.ColaPrioridadAyudaEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.EstudianteEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.GrupoEstudioEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.SolicitudAyudaEntity;
import com.plataformaEducativa.proyectoestructuradatos.exception.EntityNotFoundException;
import com.plataformaEducativa.proyectoestructuradatos.exception.UnauthorizedOperationException;
import com.plataformaEducativa.proyectoestructuradatos.repository.ColaPrioridadAyudaRepository;
import com.plataformaEducativa.proyectoestructuradatos.repository.GrupoEstudioRepository;
import com.plataformaEducativa.proyectoestructuradatos.repository.SolicitudAyudaRepository;
import com.plataformaEducativa.proyectoestructuradatos.repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class SolicitudAyudaService {

    private final SolicitudAyudaRepository solicitudAyudaRepository;
    private final ColaPrioridadAyudaRepository colaPrioridadAyudaRepository;
    private final UsuarioRepository usuarioRepository;
    private final GrupoEstudioRepository grupoEstudioRepository;

    /**
     * Crea una nueva solicitud de ayuda y la asocia a una cola de prioridad
     */
    @Transactional
    public SolicitudAyudaDto crearSolicitudAyuda(CreateSolicitudAyudaDto createDto) {
        // Buscar el estudiante
        EstudianteEntity estudiante = (EstudianteEntity) usuarioRepository.findById(createDto.getEstudianteId())
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado con ID: " + createDto.getEstudianteId()));

        // Buscar el grupo de estudio (si se proporcionó ID)
        GrupoEstudioEntity grupoEstudio = null;
        if (createDto.getGrupoEstudioId() != null) {
            grupoEstudio = grupoEstudioRepository.findById(createDto.getGrupoEstudioId())
                    .orElseThrow(() -> new EntityNotFoundException("Grupo de estudio no encontrado con ID: " + createDto.getGrupoEstudioId()));

            // Verificar que el estudiante pertenece al grupo
            if (!grupoEstudio.esMiembro(estudiante)) {
                throw new UnauthorizedOperationException("El estudiante no es miembro del grupo especificado");
            }
        }

        // Crear la solicitud de ayuda
        SolicitudAyudaEntity solicitud = SolicitudAyudaEntity.builder()
                .contenido(createDto.getContenido())
                .fechaCreacion(LocalDateTime.now())
                .leido(false)
                .estudiante(estudiante)
                .grupoEstudio(grupoEstudio)
                .build();

        // Guardar la solicitud
        solicitud = solicitudAyudaRepository.save(solicitud);

        // Crear la entrada en la cola de prioridad
        ColaPrioridadAyudaEntity colaPrioridad = ColaPrioridadAyudaEntity.builder()
                .tema(createDto.getTema())
                .nivelUrgencia(createDto.getNivelUrgencia())
                .fechaSolicitud(LocalDateTime.now())
                .resuelta(false)
                .descripcion(createDto.getDescripcion())
                .solicitudAyuda(solicitud)
                .build();

        // Guardar la cola de prioridad
        colaPrioridad = colaPrioridadAyudaRepository.save(colaPrioridad);

        // Actualizar la relación en la solicitud
        solicitud.setColaPrioridad(colaPrioridad);
        solicitudAyudaRepository.save(solicitud);

        // Convertir a DTO y devolver
        return convertToDto(solicitud);
    }

    /**
     * Obtiene todas las solicitudes de ayuda
     */
    public List<SolicitudAyudaDto> obtenerTodasLasSolicitudes() {
        return solicitudAyudaRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las solicitudes de ayuda no resueltas
     */
    public List<SolicitudAyudaDto> obtenerSolicitudesNoResueltas() {
        List<ColaPrioridadAyudaEntity> colasNoResueltas = colaPrioridadAyudaRepository.findByResueltaFalse();

        return colasNoResueltas.stream()
                .map(cola -> convertToDto(cola.getSolicitudAyuda()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una solicitud de ayuda por su ID
     */
    public SolicitudAyudaDto obtenerSolicitudPorId(UUID id) {
        SolicitudAyudaEntity solicitud = solicitudAyudaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud de ayuda no encontrada con ID: " + id));

        return convertToDto(solicitud);
    }

    /**
     * Obtiene todas las solicitudes de un estudiante
     */
    public List<SolicitudAyudaDto> obtenerSolicitudesPorEstudiante(UUID estudianteId) {
        return solicitudAyudaRepository.findByEstudianteId(estudianteId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las solicitudes de un grupo de estudio
     */
    public List<SolicitudAyudaDto> obtenerSolicitudesPorGrupo(UUID grupoId) {
        return solicitudAyudaRepository.findByGrupoEstudioId(grupoId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Marca una solicitud como leída
     */
    @Transactional
    public SolicitudAyudaDto marcarComoLeida(UUID id) {
        SolicitudAyudaEntity solicitud = solicitudAyudaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud de ayuda no encontrada con ID: " + id));

        solicitud.setLeido(true);
        solicitud = solicitudAyudaRepository.save(solicitud);

        return convertToDto(solicitud);
    }

    /**
     * Marca una solicitud como resuelta
     */
    @Transactional
    public SolicitudAyudaDto marcarComoResuelta(UUID id) {
        SolicitudAyudaEntity solicitud = solicitudAyudaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud de ayuda no encontrada con ID: " + id));

        ColaPrioridadAyudaEntity colaPrioridad = solicitud.getColaPrioridad();
        if (colaPrioridad != null) {
            colaPrioridad.setResuelta(true);
            colaPrioridadAyudaRepository.save(colaPrioridad);
        }

        return convertToDto(solicitud);
    }

    /**
     * Responde a una solicitud de ayuda
     */
    @Transactional
    public String responderSolicitud(UUID id, String respuesta) {
        SolicitudAyudaEntity solicitud = solicitudAyudaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud de ayuda no encontrada con ID: " + id));

        // Usar el método responder de la entidad
        String mensajeRespuesta = solicitud.responder(respuesta);

        // Marcar como leída
        solicitud.setLeido(true);
        solicitudAyudaRepository.save(solicitud);

        return mensajeRespuesta;
    }

    /**
     * Elimina una solicitud de ayuda
     */
    @Transactional
    public void eliminarSolicitud(UUID id) {
        SolicitudAyudaEntity solicitud = solicitudAyudaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud de ayuda no encontrada con ID: " + id));

        // Eliminar de la cola de prioridad primero (debido a la restricción de FK)
        ColaPrioridadAyudaEntity colaPrioridad = solicitud.getColaPrioridad();
        if (colaPrioridad != null) {
            colaPrioridadAyudaRepository.delete(colaPrioridad);
        }

        // Eliminar la solicitud
        solicitudAyudaRepository.delete(solicitud);
    }

    /**
     * Convierte una entidad SolicitudAyuda a DTO
     */
    private SolicitudAyudaDto convertToDto(SolicitudAyudaEntity solicitud) {
        SolicitudAyudaDto dto = new SolicitudAyudaDto();
        dto.setId(solicitud.getId());
        dto.setContenido(solicitud.getContenido());
        dto.setFechaCreacion(solicitud.getFechaCreacion());
        dto.setLeido(solicitud.isLeido());
        dto.setEstudianteId(solicitud.getEstudiante().getId());
        dto.setEstudianteNombre(solicitud.getEstudiante().getName());

        if (solicitud.getGrupoEstudio() != null) {
            dto.setGrupoEstudioId(solicitud.getGrupoEstudio().getId());
            dto.setGrupoEstudioNombre(solicitud.getGrupoEstudio().getNombreGrupo());
        }

        // Incluir información de la cola de prioridad si existe
        ColaPrioridadAyudaEntity colaPrioridad = solicitud.getColaPrioridad();
        if (colaPrioridad != null) {
            dto.setNivelUrgencia(colaPrioridad.getNivelUrgencia());
            dto.setTema(colaPrioridad.getTema());
            dto.setDescripcion(colaPrioridad.getDescripcion());
            dto.setResuelta(colaPrioridad.isResuelta());
        }

        return dto;
    }
}
