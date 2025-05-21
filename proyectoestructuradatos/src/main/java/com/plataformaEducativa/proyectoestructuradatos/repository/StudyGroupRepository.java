package com.plataformaEducativa.proyectoestructuradatos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.plataformaEducativa.proyectoestructuradatos.entity.StudyGroupEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroupEntity, UUID> {

    List<StudyGroupEntity> findByActiveTrue();

    @Query("SELECT g FROM StudyGroupEntity g WHERE :topic MEMBER OF g.topics")
    List<StudyGroupEntity> findByTopic(@Param("topic") String topic);

    @Query("SELECT g FROM StudyGroupEntity g JOIN g.members m WHERE m.id = :studentId")
    List<StudyGroupEntity> findByStudentId(@Param("studentId") UUID studentId);

    @Query("SELECT g FROM StudyGroupEntity g WHERE SIZE(g.members) < g.maxCapacity AND g.active = true")
    List<StudyGroupEntity> findAvailableGroups();

    @Query(value = "SELECT g.* FROM study_groups g " +
            "JOIN study_group_topics sgt ON g.id = sgt.group_id " +
            "WHERE sgt.topic IN (SELECT interest FROM student_interests WHERE student_id = :studentId) " +
            "AND g.id NOT IN (SELECT group_id FROM student_study_groups WHERE student_id = :studentId) " +
            "AND g.is_active = true " +
            "AND (g.max_capacity IS NULL OR (SELECT COUNT(*) FROM student_study_groups WHERE group_id = g.id) < g.max_capacity) "
            +
            "GROUP BY g.id " +
            "ORDER BY COUNT(DISTINCT sgt.topic) DESC", nativeQuery = true)
    List<StudyGroupEntity> recommendGroupsByInterests(@Param("studentId") UUID studentId);

    @Query("SELECT g FROM StudyGroupEntity g WHERE SIZE(g.members) >= :minMembers AND g.active = true")
    List<StudyGroupEntity> findActiveGroupsWithMinMembers(@Param("minMembers") int minMembers);
}
