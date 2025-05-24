package com.plataformaEducativa.proyectoestructuradatos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.plataformaEducativa.proyectoestructuradatos.entity.StudentConnectionEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentConnectionRepository extends JpaRepository<StudentConnectionEntity, UUID> {

        @Query("SELECT sc FROM StudentConnectionEntity sc WHERE " +
                        "(sc.studentA.id = :studentId1 AND sc.studentB.id = :studentId2) OR " +
                        "(sc.studentA.id = :studentId2 AND sc.studentB.id = :studentId1)")
        Optional<StudentConnectionEntity> findConnection(
                        @Param("studentId1") UUID studentId1,
                        @Param("studentId2") UUID studentId2);

        @Query("SELECT sc FROM StudentConnectionEntity sc WHERE " +
                        "sc.studentA.id = :studentId OR sc.studentB.id = :studentId")
        List<StudentConnectionEntity> findConnectionsByStudentId(@Param("studentId") UUID studentId);

        @Query("SELECT sc FROM StudentConnectionEntity sc ORDER BY sc.connectionStrength DESC")
        List<StudentConnectionEntity> findStrongestConnections();

        @Query(value = "SELECT u1.username AS student_a, u2.username AS student_b, " +
                        "sc.connection_strength FROM student_connections sc " +
                        "JOIN users u1 ON sc.student_a_id = u1.id " +
                        "JOIN users u2 ON sc.student_b_id = u2.id " +
                        "ORDER BY sc.connection_strength DESC " +
                        "LIMIT :limit", nativeQuery = true)
        List<Object[]> findTopConnections(@Param("limit") int limit);

        @Query(value = "WITH RECURSIVE connection_path AS ( " +
                        "  SELECT sc.student_a_id, sc.student_b_id, ARRAY[sc.student_a_id, sc.student_b_id] AS path, " +
                        "         1 AS depth, sc.connection_strength AS min_strength " +
                        "  FROM student_connections sc " +
                        "  WHERE sc.student_a_id = :startId OR sc.student_b_id = :startId " +
                        "  UNION " +
                        "  SELECT sc.student_a_id, sc.student_b_id, " +
                        "         CASE " +
                        "           WHEN cp.path[array_length(cp.path, 1)] = sc.student_a_id THEN cp.path || sc.student_b_id "
                        +
                        "           ELSE cp.path || sc.student_a_id " +
                        "         END, " +
                        "         cp.depth + 1, " +
                        "         LEAST(cp.min_strength, sc.connection_strength) " +
                        "  FROM student_connections sc " +
                        "  JOIN connection_path cp ON " +
                        "    (cp.path[array_length(cp.path, 1)] = sc.student_a_id AND NOT sc.student_b_id = ANY(cp.path)) OR "
                        +
                        "    (cp.path[array_length(cp.path, 1)] = sc.student_b_id AND NOT sc.student_a_id = ANY(cp.path)) "
                        +
                        "  WHERE cp.depth < 10 " +
                        ") " +
                        "SELECT cp.path, cp.depth, cp.min_strength " +
                        "FROM connection_path cp " +
                        "WHERE :endId = ANY(cp.path) " +
                        "ORDER BY cp.depth ASC, cp.min_strength DESC " +
                        "LIMIT 1", nativeQuery = true)
        Optional<Object[]> findShortestPath(@Param("startId") UUID startId, @Param("endId") UUID endId);

        @Query(value = "SELECT sc.*, " +
                        "(SELECT COUNT(*) FROM student_connections WHERE student_a_id = sc.student_a_id OR student_b_id = sc.student_a_id) + "
                        +
                        "(SELECT COUNT(*) FROM student_connections WHERE student_a_id = sc.student_b_id OR student_b_id = sc.student_b_id) "
                        +
                        "AS total_connections " +
                        "FROM student_connections sc " +
                        "ORDER BY total_connections DESC", nativeQuery = true)
        List<Object[]> findConnectionsBetweenMostConnectedStudents();
}
