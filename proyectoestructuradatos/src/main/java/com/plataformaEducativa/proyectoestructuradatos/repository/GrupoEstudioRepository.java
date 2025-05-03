package com.plataformaEducativa.proyectoestructuradatos.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.plataformaEducativa.proyectoestructuradatos.entity.EstudianteEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.GrupoEstudioEntity;

public interface GrupoEstudioRepository extends JpaRepository<GrupoEstudioEntity, UUID> {

    // Buscar grupos por nombre
    List<GrupoEstudioEntity> findByNombreGrupoContainingIgnoreCase(String nombre);

    // Buscar grupos activos
    List<GrupoEstudioEntity> findByActivoTrue();

    // Buscar grupos inactivos
    List<GrupoEstudioEntity> findByActivoFalse();

    // Buscar grupos a los que pertenece un estudiante específico
    @Query("SELECT g FROM GrupoEstudioEntity g JOIN g.estudiantes e WHERE e = :estudiante")
    List<GrupoEstudioEntity> findGruposByEstudiante(@Param("estudiante") EstudianteEntity estudiante);

    // Buscar grupos por ID de estudiante
    @Query("SELECT g FROM GrupoEstudioEntity g JOIN g.estudiantes e WHERE e.id = :estudianteId")
    List<GrupoEstudioEntity> findGruposByEstudianteId(@Param("estudianteId") UUID estudianteId);

    // Verificar si un estudiante es miembro de un grupo
    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM GrupoEstudioEntity g JOIN g.estudiantes e WHERE g.id = :grupoId AND e.id = :estudianteId")
    boolean isEstudianteMiembroDeGrupo(@Param("grupoId") UUID grupoId, @Param("estudianteId") UUID estudianteId);
}
