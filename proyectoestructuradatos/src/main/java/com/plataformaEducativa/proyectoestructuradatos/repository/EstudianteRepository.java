package com.plataformaEducativa.proyectoestructuradatos.repository;

import com.plataformaEducativa.proyectoestructuradatos.entity.EstudianteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EstudianteRepository extends JpaRepository<EstudianteEntity, UUID> {
    // Métodos adicionales si son necesarios
}
