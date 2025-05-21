package com.plataformaEducativa.proyectoestructuradatos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.plataformaEducativa.proyectoestructuradatos.entity.ModeratorEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ModeratorRepository extends JpaRepository<ModeratorEntity, UUID> {

    Optional<ModeratorEntity> findByUsername(String username);

    @Query("SELECT m FROM ModeratorEntity m WHERE m.department = :department")
    List<ModeratorEntity> findByDepartment(@Param("department") String department);

    @Query("SELECT m FROM ModeratorEntity m WHERE m.specialization = :specialization")
    List<ModeratorEntity> findBySpecialization(@Param("specialization") String specialization);

    @Query("SELECT m FROM ModeratorEntity m WHERE m.accessLevel >= :level")
    List<ModeratorEntity> findByMinimumAccessLevel(@Param("level") Integer level);
}
