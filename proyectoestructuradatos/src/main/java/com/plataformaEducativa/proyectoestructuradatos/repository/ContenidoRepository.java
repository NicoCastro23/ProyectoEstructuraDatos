package com.plataformaEducativa.proyectoestructuradatos.repository;

import com.plataformaEducativa.proyectoestructuradatos.entity.ContentEntity;
import com.plataformaEducativa.proyectoestructuradatos.enums.TipoContenido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContenidoRepository extends JpaRepository<ContentEntity, UUID> {

    /**
     * Busca contenidos por tipo
     */
    List<ContentEntity> findByTipo(TipoContenido tipo);

    /**
     * Busca contenidos por el ID del autor
     */
    List<ContentEntity> findByAutorId(UUID autorId);

    /**
     * Busca contenidos por tipo y por el ID del autor
     */
    List<ContentEntity> findByTipoAndAutorId(TipoContenido tipo, UUID autorId);

    /**
     * Busca contenidos cuyo título contiene la cadena de búsqueda (case
     * insensitive)
     */
    List<ContentEntity> findByTituloContainingIgnoreCase(String titulo);

    /**
     * Busca los contenidos mejor valorados
     */
    @Query("SELECT c FROM ContentEntity c WHERE c.promedioValoracion >= :minValoracion ORDER BY c.promedioValoracion DESC")
    List<ContentEntity> findTopRated(@Param("minValoracion") Double minValoracion);
}