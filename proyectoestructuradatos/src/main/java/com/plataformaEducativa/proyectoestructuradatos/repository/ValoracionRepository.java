package com.plataformaEducativa.proyectoestructuradatos.repository;

import com.plataformaEducativa.proyectoestructuradatos.entity.ValoracionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ValoracionRepository extends JpaRepository<ValoracionEntity, UUID> {

    /**
     * Busca todas las valoraciones de un contenido
     */
    List<ValoracionEntity> findByContenidoId(UUID contenidoId);

    /**
     * Busca todas las valoraciones hechas por un estudiante
     */
    List<ValoracionEntity> findByEstudianteId(UUID estudianteId);

    /**
     * Busca una valoración de un contenido hecha por un estudiante específico
     */
    Optional<ValoracionEntity> findByContenidoIdAndEstudianteId(UUID contenidoId, UUID estudianteId);

    /**
     * Calcula el promedio de puntuaciones para un contenido específico
     */
    @Query("SELECT AVG(v.puntuacion) FROM ValoracionEntity v WHERE v.contenido.id = :contenidoId")
    Double calcularPromedioPorContenido(@Param("contenidoId") UUID contenidoId);
}