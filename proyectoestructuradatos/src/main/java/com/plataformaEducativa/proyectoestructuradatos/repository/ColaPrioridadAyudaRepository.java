package com.plataformaEducativa.proyectoestructuradatos.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.plataformaEducativa.proyectoestructuradatos.entity.ColaPrioridadAyudaEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.ColaPrioridadAyudaEntity.NivelUrgencia;

public interface ColaPrioridadAyudaRepository extends JpaRepository<ColaPrioridadAyudaEntity, UUID> {

    // Buscar solicitudes por nivel de urgencia
    List<ColaPrioridadAyudaEntity> findByNivelUrgencia(NivelUrgencia nivelUrgencia);

    // Buscar solicitudes no resueltas
    List<ColaPrioridadAyudaEntity> findByResueltaFalse();

    // Buscar solicitudes resueltas
    List<ColaPrioridadAyudaEntity> findByResueltaTrue();

    // Buscar solicitudes por nivel de urgencia y no resueltas, ordenadas por fecha (más antiguas primero)
    List<ColaPrioridadAyudaEntity> findByNivelUrgenciaAndResueltaFalseOrderByFechaSolicitudAsc(NivelUrgencia nivelUrgencia);

    // Consulta personalizada para obtener la cola de prioridad ordenada por criterio de prioridad
    // Esta consulta solo es una aproximación ya que el cálculo real de prioridad requiere lógica de programación
    @Query("SELECT c FROM ColaPrioridadAyudaEntity c WHERE c.resuelta = false ORDER BY " +
            "CASE c.nivelUrgencia " +
            "  WHEN 'CRITICA' THEN 4 " +
            "  WHEN 'ALTA' THEN 3 " +
            "  WHEN 'MEDIA' THEN 2 " +
            "  WHEN 'BAJA' THEN 1 " +
            "  ELSE 0 END DESC, c.fechaSolicitud ASC")
    List<ColaPrioridadAyudaEntity> findColaPrioridadOrdenada();

    // Buscar por tema
    List<ColaPrioridadAyudaEntity> findByTemaContainingIgnoreCase(String tema);

    // Buscar por ID de solicitud relacionada
    ColaPrioridadAyudaEntity findBySolicitudAyudaId(UUID solicitudId);
}
