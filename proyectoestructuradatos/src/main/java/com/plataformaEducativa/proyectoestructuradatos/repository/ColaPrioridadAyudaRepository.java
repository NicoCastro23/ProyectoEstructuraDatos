package com.plataformaEducativa.proyectoestructuradatos.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.plataformaEducativa.proyectoestructuradatos.entity.ColaPrioridadAyudaEntity;
import com.plataformaEducativa.proyectoestructuradatos.enums.NivelUrgencia;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColaPrioridadAyudaRepository extends JpaRepository<ColaPrioridadAyudaEntity, UUID> {

    List<ColaPrioridadAyudaEntity> findByNivelUrgencia(NivelUrgencia nivelUrgencia);

    List<ColaPrioridadAyudaEntity> findByResueltaFalse();

    List<ColaPrioridadAyudaEntity> findByResueltaTrue();

    List<ColaPrioridadAyudaEntity> findByNivelUrgenciaAndResueltaFalseOrderByFechaSolicitudAsc(
            NivelUrgencia nivelUrgencia);

    /**
     * Aproximación para ordenar por prioridad basada en nivel de urgencia (sin
     * cálculo dinámico).
     * Para cálculo completo con tiempo de espera, usar lógica de negocio en el
     * servicio.
     */
    @Query("""
            SELECT c FROM ColaPrioridadAyudaEntity c
            WHERE c.resuelta = false
            ORDER BY
              CASE c.nivelUrgencia
                WHEN 'CRITICA' THEN 4
                WHEN 'ALTA' THEN 3
                WHEN 'MEDIA' THEN 2
                WHEN 'BAJA' THEN 1
                ELSE 0
              END DESC,
              c.fechaSolicitud ASC
            """)
    List<ColaPrioridadAyudaEntity> findAllPendingOrderedByUrgencyAndDate();

    List<ColaPrioridadAyudaEntity> findByTemaContainingIgnoreCase(String tema);

    Optional<ColaPrioridadAyudaEntity> findBySolicitudAyudaId(UUID solicitudId);
}
