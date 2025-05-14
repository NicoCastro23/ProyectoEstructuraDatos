package com.plataformaEducativa.proyectoestructuradatos.mapper;

import com.plataformaEducativa.proyectoestructuradatos.dto.*;
import com.plataformaEducativa.proyectoestructuradatos.entity.*;
import com.plataformaEducativa.proyectoestructuradatos.enums.TipoContenido;
import com.plataformaEducativa.proyectoestructuradatos.exception.TipoContenidoIncompatibleException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.plataformaEducativa.proyectoestructuradatos.enums.TipoContenido.*;

@Component
public class ContentMapper {

    /**
     * Convierte una entidad de contenido a su correspondiente DTO según su tipo
     */
    public ContenidoDto toDTO(ContentEntity entity) {
        // Crear base DTO según el tipo de contenido
        ContenidoDto dto;

        switch (entity.getTipo()) {
            case DOCUMENTO:
                DocumentoDto documentoDTO = new DocumentoDto();
                // Aquí se obtendrían los campos específicos del documento desde una tabla
                // relacionada o de metadatos
                documentoDTO.setRutaArchivo("ruta/al/documento/" + entity.getId());
                //
                documentoDTO.setFormato("PDF"); // Ejemplo, esto podría venir de metadatos
                dto = documentoDTO;
                break;

            case ENLACE:
                EnlaceDto enlaceDTO = new EnlaceDto();
                // Campos específicos del enlace
                enlaceDTO.setUrl("https://ejemplo.com/" + entity.getId()); // Ejemplo
                enlaceDTO.setSitioWeb("Ejemplo.com"); // Ejemplo
                dto = enlaceDTO;
                break;

            case VIDEO:
                VideoDto videoDTO = new VideoDto();
                // Campos específicos del video
                videoDTO.setUrl("https://video.ejemplo.com/" + entity.getId());
                videoDTO.setDuracionSegundos(300); // Ejemplo, 5 minutos
                videoDTO.setPlataforma("YouTube"); // Ejemplo
                dto = videoDTO;
                break;

            case IMAGEN:
                ImagenDto imagenDTO = new ImagenDto();
                // Campos específicos de la imagen
                imagenDTO.setRutaImagen("ruta/a/imagen/" + entity.getId());
                imagenDTO.setFormatoImagen("JPG"); // Ejemplo
                imagenDTO.setAncho(800); // Ejemplo
                imagenDTO.setAlto(600); // Ejemplo
                dto = imagenDTO;
                break;

            case CODIGO:
                CodigoDto codigoDTO = new CodigoDto();
                // Campos específicos del código
                codigoDTO.setCodigo("// Ejemplo de código\npublic class Main { ... }");
                codigoDTO.setLenguaje("Java"); // Ejemplo
                dto = codigoDTO;
                break;

            default:
                throw new IllegalArgumentException("Tipo de contenido no soportado: " + entity.getTipo());
        }

        // Establecer campos comunes
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setDescripcion(entity.getDescripcion());
        dto.setTipo(dto.getTipo());
        dto.setAutorId(entity.getAutor().getId());
        dto.setPromedioValoracion(entity.getPromedioValoracion());

        return dto;
    }

    /**
     * Convierte un DTO de documento a una entidad de contenido
     */
    public ContentEntity fromDocumentoDTO(DocumentoDto dto, UsuarioEntity autor) {
        validateTipoContenido(dto, TipoContenido.DOCUMENTO);
        return createBaseContenidoEntity(dto, autor);
        // Aquí se guardarían campos específicos en una tabla adicional o metadatos
    }

    /**
     * Convierte un DTO de enlace a una entidad de contenido
     */
    public ContentEntity fromEnlaceDTO(EnlaceDto dto, UsuarioEntity autor) {
        validateTipoContenido(dto, ENLACE);
        return createBaseContenidoEntity(dto, autor);
        // Aquí se guardarían campos específicos en una tabla adicional o metadatos
    }

    /**
     * Convierte un DTO de video a una entidad de contenido
     */
    public ContentEntity fromVideoDTO(VideoDto dto, UsuarioEntity autor) {
        validateTipoContenido(dto, TipoContenido.VIDEO);
        return createBaseContenidoEntity(dto, autor);
        // Aquí se guardarían campos específicos en una tabla adicional o metadatos
    }

    /**
     * Convierte un DTO de imagen a una entidad de contenido
     */
    public ContentEntity fromImagenDTO(ImagenDto dto, UsuarioEntity autor) {
        validateTipoContenido(dto, IMAGEN);
        return createBaseContenidoEntity(dto, autor);
        // Aquí se guardarían campos específicos en una tabla adicional o metadatos
    }

    /**
     * Convierte un DTO de código a una entidad de contenido
     */
    public ContentEntity fromCodigoDTO(CodigoDto dto, UsuarioEntity autor) {
        validateTipoContenido(dto, CODIGO);
        return createBaseContenidoEntity(dto, autor);
        // Aquí se guardarían campos específicos en una tabla adicional o metadatos
    }

    /**
     * Crea una entidad base de contenido con los campos comunes
     */
    private ContentEntity createBaseContenidoEntity(ContenidoDto dto, UsuarioEntity autor) {
        ContentEntity entity = new ContentEntity();

        entity.setTitulo(dto.getTitulo());
        entity.setDescripcion(dto.getDescripcion());
        entity.setTipo(dto.getTipo());
        entity.setFechaPublicacion(LocalDateTime.now());
        entity.setAutor(autor);
        entity.setPromedioValoracion(0.0); // Valor inicial

        return entity;
    }

    /**
     * Valida que el tipo de contenido del DTO coincida con el esperado
     */
    private void validateTipoContenido(ContenidoDto dto, TipoContenido tipoEsperado) {
        if (dto.getTipo() != tipoEsperado) {
            throw new TipoContenidoIncompatibleException(tipoEsperado, dto.getTipo());
        }
    }
}
