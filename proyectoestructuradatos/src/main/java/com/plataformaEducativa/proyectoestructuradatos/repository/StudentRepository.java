package com.plataformaEducativa.proyectoestructuradatos.repository;

import com.plataformaEducativa.proyectoestructuradatos.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, UUID> {

    Optional<StudentEntity> findByUsername(String username);

    @Query("SELECT s FROM StudentEntity s WHERE :interest MEMBER OF s.academicInterests")
    List<StudentEntity> findByAcademicInterest(@Param("interest") String interest);

    @Query("SELECT s FROM StudentEntity s WHERE s.fieldOfStudy = :field")
    List<StudentEntity> findByFieldOfStudy(@Param("field") String field);

    @Query("SELECT s FROM StudentEntity s WHERE s.educationLevel = :level")
    List<StudentEntity> findByEducationLevel(@Param("level") String level);

    @Query("SELECT DISTINCT s FROM StudentEntity s JOIN s.studyGroups g WHERE g.id = :groupId")
    List<StudentEntity> findByStudyGroupId(@Param("groupId") UUID groupId);

    @Query(value = "SELECT s.* FROM students s " +
            "JOIN student_study_groups ssg ON s.user_id = ssg.student_id " +
            "JOIN study_groups g ON ssg.group_id = g.id " +
            "WHERE g.id IN (SELECT group_id FROM student_study_groups WHERE student_id = :studentId) " +
            "AND s.user_id != :studentId " +
            "GROUP BY s.user_id " +
            "ORDER BY COUNT(DISTINCT g.id) DESC", nativeQuery = true)
    List<StudentEntity> findStudentsWithCommonGroups(@Param("studentId") UUID studentId);
}
