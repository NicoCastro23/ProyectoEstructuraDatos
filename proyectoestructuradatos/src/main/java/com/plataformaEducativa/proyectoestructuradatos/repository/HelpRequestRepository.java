package com.plataformaEducativa.proyectoestructuradatos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.plataformaEducativa.proyectoestructuradatos.entity.HelpRequestEntity;
import com.plataformaEducativa.proyectoestructuradatos.enums.HelpRequestPriority;

import java.util.List;
import java.util.UUID;

@Repository
public interface HelpRequestRepository extends JpaRepository<HelpRequestEntity, UUID> {

        /**
         * Encuentra todas las solicitudes de ayuda creadas por un estudiante específico
         * 
         * @param requesterId UUID del estudiante que creó las solicitudes
         * @return Lista de solicitudes de ayuda
         */
        @Query("SELECT h FROM HelpRequestEntity h WHERE h.requester.id = :requesterId ORDER BY h.createdAt DESC")
        List<HelpRequestEntity> findByRequesterId(@Param("requesterId") UUID requesterId);

        List<HelpRequestEntity> findByHelperId(UUID helperId);

        List<HelpRequestEntity> findByResolvedFalseOrderByPriorityDescCreatedAtAsc();

        List<HelpRequestEntity> findByTopicAndResolvedFalse(String topic);

        List<HelpRequestEntity> findByPriorityAndResolvedFalse(HelpRequestPriority priority);

        @Query("SELECT hr FROM HelpRequestEntity hr WHERE hr.resolved = false AND " +
                        "(LOWER(hr.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(hr.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(hr.topic) LIKE LOWER(CONCAT('%', :keyword, '%')))")
        List<HelpRequestEntity> searchActiveRequestsByKeyword(@Param("keyword") String keyword);

        @Query(value = "SELECT hr.* FROM help_requests hr " +
                        "WHERE hr.resolved = false " +
                        "AND hr.topic IN (SELECT interest FROM student_interests WHERE student_id = :studentId) " +
                        "AND hr.requester_id != :studentId " +
                        "AND hr.helper_id IS NULL " +
                        "ORDER BY CASE " +
                        "   WHEN hr.priority = 'URGENT' THEN 1 " +
                        "   WHEN hr.priority = 'HIGH' THEN 2 " +
                        "   WHEN hr.priority = 'MEDIUM' THEN 3 " +
                        "   ELSE 4 END, " +
                        "hr.created_at ASC", nativeQuery = true)
        List<HelpRequestEntity> findRequestsMatchingStudentInterests(@Param("studentId") UUID studentId);
}
