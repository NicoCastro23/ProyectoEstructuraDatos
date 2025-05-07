package com.plataformaEducativa.proyectoestructuradatos.service;

import com.plataformaEducativa.proyectoestructuradatos.dto.CrearValoracionDto;
import com.plataformaEducativa.proyectoestructuradatos.dto.ValoracionDto;
import com.plataformaEducativa.proyectoestructuradatos.entity.ContenidoEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.EstudianteEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.ValoracionEntity;
import com.plataformaEducativa.proyectoestructuradatos.exception.RecursoNoEncontradoException;
import com.plataformaEducativa.proyectoestructuradatos.exception.ValoracionDuplicadaException;
import com.plataformaEducativa.proyectoestructuradatos.mapper.ValoracionMapper;
import com.plataformaEducativa.proyectoestructuradatos.repository.ContenidoRepository;
import com.plataformaEducativa.proyectoestructuradatos.repository.EstudianteRepository;
import com.plataformaEducativa.proyectoestructuradatos.repository.ValoracionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValoracionService {

    private final ValoracionRepository valoracionRepository;
    private final ContenidoRepository contenidoRepository;
    private final EstudianteRepository estudianteRepository;
    private final ValoracionMapper valoracionMapper;
    private final ContenidoService contenidoService;

    /**
     * Crea una nueva valoración para un contenido
     * @param dto DTO con los datos de la valoración
     * @param estudianteId ID del estudiante que valora
     * @return DTO de la valoración creada
     */
    @Transactional
    public ValoracionDto crearValoracion(CrearValoracionDto dto, UUID estudianteId) {
        // Validar que el contenido existe
        ContenidoEntity contenido = contenidoRepository.findById(dto.getContenidoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Contenido", "id", dto.getContenidoId()));

        // Validar que el estudiante existe
        EstudianteEntity estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante", "id", estudianteId));

        // Verificar que el estudiante no haya valorado ya este contenido
        valoracionRepository.findByContenidoIdAndEstudianteId(dto.getContenidoId(), estudianteId)
                .ifPresent(v -> {
                    throw new ValoracionDuplicadaException("El estudiante ya ha valorado este contenido");
                });

        // Crear y guardar la valoración
        ValoracionEntity valoracion = valoracionMapper.fromCrearValoracionDTO(dto, contenido, estudiante);
        valoracion = valoracionRepository.save(valoracion);

        // Actualizar el promedio de valoraciones del contenido
        actualizarPromedioValoracion(dto.getContenidoId());

        return valoracionMapper.toDTO(valoracion);
    }

    /**
     * Obtiene todas las valoraciones de un contenido
     * @param contenidoId ID del contenido
     * @return Lista de DTOs de valoraciones
     */
    @Transactional(readOnly = true)
    public List<ValoracionDto> obtenerValoracionesPorContenido(UUID contenidoId) {
        // Verificar que el contenido existe
        if (!contenidoRepository.existsById(contenidoId)) {
            throw new RecursoNoEncontradoException("Contenido", "id", contenidoId);
        }

        return valoracionRepository.findByContenidoId(contenidoId).stream()
                .map(valoracionMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las valoraciones hechas por un estudiante
     * @param estudianteId ID del estudiante
     * @return Lista de DTOs de valoraciones
     */
    @Transactional(readOnly = true)
    public List<ValoracionDto> obtenerValoracionesPorEstudiante(UUID estudianteId) {
        // Verificar que el estudiante existe
        if (!estudianteRepository.existsById(estudianteId)) {
            throw new RecursoNoEncontradoException("Estudiante", "id", estudianteId);
        }

        return valoracionRepository.findByEstudianteId(estudianteId).stream()
                .map(valoracionMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Elimina una valoración
     * @param valoracionId ID de la valoración
     * @param estudianteId ID del estudiante que hizo la valoración
     */
    @Transactional
    public void eliminarValoracion(UUID valoracionId, UUID estudianteId) {
        ValoracionEntity valoracion = valoracionRepository.findById(valoracionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Valoración", "id", valoracionId));

        // Verificar que la valoración pertenece al estudiante
        if (!valoracion.getEstudiante().getId().equals(estudianteId)) {
            throw new ValoracionDuplicadaException("No tienes permiso para eliminar esta valoración");
        }

        UUID contenidoId = valoracion.getContenido().getId();
        valoracionRepository.delete(valoracion);

        // Actualizar el promedio de valoraciones del contenido
        actualizarPromedioValoracion(contenidoId);
    }

    /**
     * Actualiza el promedio de valoraciones de un contenido
     * @param contenidoId ID del contenido
     */
    private void actualizarPromedioValoracion(UUID contenidoId) {
        Double promedio = valoracionRepository.calcularPromedioPorContenido(contenidoId);
        contenidoService.actualizarPromedioValoracion(contenidoId, promedio != null ? promedio : 0.0);
    }
}
