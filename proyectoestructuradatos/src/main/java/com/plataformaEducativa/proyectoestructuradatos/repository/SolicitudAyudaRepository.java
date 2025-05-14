package com.plataformaEducativa.proyectoestructuradatos.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.plataformaEducativa.proyectoestructuradatos.entity.EstudianteEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.GrupoEstudioEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.SolicitudAyudaEntity;

public interface SolicitudAyudaRepository extends JpaRepository<SolicitudAyudaEntity, UUID> {

    // Buscar solicitudes por estudiante
    List<SolicitudAyudaEntity> findByEstudiante(EstudianteEntity estudiante);

    // Buscar solicitudes por grupo de estudio
    List<SolicitudAyudaEntity> findByGrupoEstudio(GrupoEstudioEntity grupoEstudio);

    // Buscar solicitudes no leídas
    List<SolicitudAyudaEntity> findByLeidoFalse();

    // Buscar solicitudes por estudiante y estado de lectura
    List<SolicitudAyudaEntity> findByEstudianteAndLeidoFalse(EstudianteEntity estudiante);

    // Buscar solicitudes por grupo de estudio y estado de lectura
    List<SolicitudAyudaEntity> findByGrupoEstudioAndLeidoFalse(GrupoEstudioEntity grupoEstudio);

    // Consulta personalizada para buscar solicitudes sin asignar a cola de
    // prioridad
    @Query("SELECT s FROM SolicitudAyudaEntity s WHERE s.colaPrioridad IS NULL")
    List<SolicitudAyudaEntity> findSolicitudesSinColaPrioridad();

    // Consulta para buscar solicitudes por ID de estudiante
    List<SolicitudAyudaEntity> findByEstudianteId(UUID estudianteId);

    // Consulta para buscar solicitudes por ID de grupo
    List<SolicitudAyudaEntity> findByGrupoEstudioId(UUID grupoId);
}
