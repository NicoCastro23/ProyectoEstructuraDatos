package com.plataformaEducativa.proyectoestructuradatos.repository;

import com.plataformaEducativa.proyectoestructuradatos.entity.ContenidoEntity;
import com.plataformaEducativa.proyectoestructuradatos.enums.TipoContenido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContenidoRepository extends JpaRepository<ContenidoEntity, UUID> {

    /**
     * Busca contenidos por tipo
     */
    List<ContenidoEntity> findByTipo(TipoContenido tipo);

    /**
     * Busca contenidos por el ID del autor
     */
    List<ContenidoEntity> findByAutorId(UUID autorId);

    /**
     * Busca contenidos por tipo y por el ID del autor
     */
    List<ContenidoEntity> findByTipoAndAutorId(TipoContenido tipo, UUID autorId);

    /**
     * Busca contenidos cuyo título contiene la cadena de búsqueda (case insensitive)
     */
    List<ContenidoEntity> findByTituloContainingIgnoreCase(String titulo);

    /**
     * Busca los contenidos mejor valorados
     */
    @Query("SELECT c FROM ContenidoEntity c WHERE c.promedioValoracion >= :minValoracion ORDER BY c.promedioValoracion DESC")
    List<ContenidoEntity> findTopRated(@Param("minValoracion") Double minValoracion);
}