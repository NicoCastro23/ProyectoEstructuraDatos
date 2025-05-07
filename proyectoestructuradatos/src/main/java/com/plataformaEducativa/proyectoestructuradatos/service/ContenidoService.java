package com.plataformaEducativa.proyectoestructuradatos.service;

import com.plataformaEducativa.proyectoestructuradatos.dto.*;
import com.plataformaEducativa.proyectoestructuradatos.entity.ContenidoEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.EstudianteEntity;
import com.plataformaEducativa.proyectoestructuradatos.enums.TipoContenido;
import com.plataformaEducativa.proyectoestructuradatos.exception.DatosInvalidosException;
import com.plataformaEducativa.proyectoestructuradatos.exception.RecursoNoEncontradoException;
import com.plataformaEducativa.proyectoestructuradatos.mapper.ContenidoMapper;
import com.plataformaEducativa.proyectoestructuradatos.repository.ContenidoRepository;
import com.plataformaEducativa.proyectoestructuradatos.repository.EstudianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContenidoService {

    private final ContenidoRepository contenidoRepository;
    private final EstudianteRepository estudianteRepository;
    private final ContenidoMapper contenidoMapper;

    /**
     * Crea un nuevo documento
     * @param dto DTO con los datos del documento
     * @param autorId ID del estudiante autor
     * @return DTO del contenido creado
     */
    @Transactional
    public ContenidoDto crearDocumento(DocumentoDto dto, UUID autorId) {
        EstudianteEntity autor = obtenerEstudiante(autorId);
        validarTipoContenido(dto, TipoContenido.DOCUMENTO);

        ContenidoEntity entity = contenidoMapper.fromDocumentoDTO(dto, autor);
        entity = contenidoRepository.save(entity);

        // Aquí se guardarían detalles específicos del documento en otra tabla o estructura
        // Por ejemplo, almacenar la ruta del archivo, formato, etc.

        return contenidoMapper.toDTO(entity);
    }

    /**
     * Crea un nuevo enlace
     * @param dto DTO con los datos del enlace
     * @param autorId ID del estudiante autor
     * @return DTO del contenido creado
     */
    @Transactional
    public ContenidoDto crearEnlace(EnlaceDto dto, UUID autorId) {
        EstudianteEntity autor = obtenerEstudiante(autorId);
        validarTipoContenido(dto, TipoContenido.ENLACE);

        ContenidoEntity entity = contenidoMapper.fromEnlaceDTO(dto, autor);
        entity = contenidoRepository.save(entity);

        // Aquí se guardarían detalles específicos del enlace en otra tabla o estructura
        // Por ejemplo, almacenar la URL, nombre del sitio, etc.

        return contenidoMapper.toDTO(entity);
    }

    /**
     * Crea un nuevo video
     * @param dto DTO con los datos del video
     * @param autorId ID del estudiante autor
     * @return DTO del contenido creado
     */
    @Transactional
    public ContenidoDto crearVideo(VideoDto dto, UUID autorId) {
        EstudianteEntity autor = obtenerEstudiante(autorId);
        validarTipoContenido(dto, TipoContenido.VIDEO);

        ContenidoEntity entity = contenidoMapper.fromVideoDTO(dto, autor);
        entity = contenidoRepository.save(entity);

        // Aquí se guardarían detalles específicos del video en otra tabla o estructura
        // Por ejemplo, almacenar la URL, duración, plataforma, etc.

        return contenidoMapper.toDTO(entity);
    }

    /**
     * Crea una nueva imagen
     * @param dto DTO con los datos de la imagen
     * @param autorId ID del estudiante autor
     * @return DTO del contenido creado
     */
    @Transactional
    public ContenidoDto crearImagen(ImagenDto dto, UUID autorId) {
        EstudianteEntity autor = obtenerEstudiante(autorId);
        validarTipoContenido(dto, TipoContenido.IMAGEN);

        ContenidoEntity entity = contenidoMapper.fromImagenDTO(dto, autor);
        entity = contenidoRepository.save(entity);

        // Aquí se guardarían detalles específicos de la imagen en otra tabla o estructura
        // Por ejemplo, almacenar la ruta, dimensiones, formato, etc.

        return contenidoMapper.toDTO(entity);
    }

    /**
     * Crea un nuevo código
     * @param dto DTO con los datos del código
     * @param autorId ID del estudiante autor
     * @return DTO del contenido creado
     */
    @Transactional
    public ContenidoDto crearCodigo(CodigoDto dto, UUID autorId) {
        EstudianteEntity autor = obtenerEstudiante(autorId);
        validarTipoContenido(dto, TipoContenido.CODIGO);

        ContenidoEntity entity = contenidoMapper.fromCodigoDTO(dto, autor);
        entity = contenidoRepository.save(entity);

        // Aquí se guardarían detalles específicos del código en otra tabla o estructura
        // Por ejemplo, almacenar el código fuente, lenguaje, etc.

        return contenidoMapper.toDTO(entity);
    }

    /**
     * Obtiene un contenido por su ID
     * @param contenidoId ID del contenido
     * @return DTO del contenido
     */
    @Transactional(readOnly = true)
    public ContenidoDto obtenerContenidoPorId(UUID contenidoId) {
        ContenidoEntity entity = contenidoRepository.findById(contenidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Contenido", "id", contenidoId));

        return contenidoMapper.toDTO(entity);
    }

    /**
     * Obtiene todos los contenidos
     * @return Lista de DTOs de contenidos
     */
    @Transactional(readOnly = true)
    public List<ContenidoDto> obtenerTodosLosContenidos() {
        return contenidoRepository.findAll().stream()
                .map(contenidoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene contenidos por tipo
     * @param tipo Tipo de contenido
     * @return Lista de DTOs de contenidos
     */
    @Transactional(readOnly = true)
    public List<ContenidoDto> obtenerContenidosPorTipo(TipoContenido tipo) {
        return contenidoRepository.findByTipo(tipo).stream()
                .map(contenidoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene contenidos por autor
     * @param autorId ID del autor
     * @return Lista de DTOs de contenidos
     */
    @Transactional(readOnly = true)
    public List<ContenidoDto> obtenerContenidosPorAutor(UUID autorId) {
        return contenidoRepository.findByAutorId(autorId).stream()
                .map(contenidoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Elimina un contenido
     * @param contenidoId ID del contenido
     */
    @Transactional
    public void eliminarContenido(UUID contenidoId) {
        if (!contenidoRepository.existsById(contenidoId)) {
            throw new RecursoNoEncontradoException("Contenido", "id", contenidoId);
        }
        contenidoRepository.deleteById(contenidoId);
    }

    /**
     * Actualiza el promedio de valoraciones de un contenido
     * @param contenidoId ID del contenido
     * @param nuevoPromedio Nuevo promedio de valoraciones
     */
    @Transactional
    public void actualizarPromedioValoracion(UUID contenidoId, Double nuevoPromedio) {
        ContenidoEntity entity = contenidoRepository.findById(contenidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Contenido", "id", contenidoId));

        entity.setPromedioValoracion(nuevoPromedio);
        contenidoRepository.save(entity);
    }

    /**
     * Obtiene un estudiante por su ID
     * @param estudianteId ID del estudiante
     * @return Entidad del estudiante
     */
    private EstudianteEntity obtenerEstudiante(UUID estudianteId) {
        return estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante", "id", estudianteId));
    }

    /**
     * Valida que el tipo de contenido del DTO coincida con el esperado
     * @param dto DTO del contenido
     * @param tipoEsperado Tipo de contenido esperado
     */
    private void validarTipoContenido(ContenidoDto dto, TipoContenido tipoEsperado) {
        if (dto.getTipo() != tipoEsperado) {
            throw new DatosInvalidosException("El tipo de contenido debe ser " + tipoEsperado);
        }
    }
}