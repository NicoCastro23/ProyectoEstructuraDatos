package com.plataformaEducativa.proyectoestructuradatos.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.plataformaEducativa.proyectoestructuradatos.entity.ContentEntity;
import com.plataformaEducativa.proyectoestructuradatos.enums.ContentType;

import java.util.UUID;

@Repository
public interface ContentRepository extends JpaRepository<ContentEntity, UUID> {

    Page<ContentEntity> findByAuthorId(UUID authorId, Pageable pageable);

    Page<ContentEntity> findByContentType(ContentType contentType, Pageable pageable);

    @Query("SELECT c FROM ContentEntity c WHERE :tag MEMBER OF c.tags")
    Page<ContentEntity> findByTag(@Param("tag") String tag, Pageable pageable);

    @Query("SELECT c FROM ContentEntity c WHERE " +
            "LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<ContentEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM ContentEntity c ORDER BY c.averageRating DESC, c.ratingCount DESC")
    Page<ContentEntity> findTopRated(Pageable pageable);

    @Query("SELECT c FROM ContentEntity c ORDER BY c.viewCount DESC")
    Page<ContentEntity> findMostViewed(Pageable pageable);

    @Query("SELECT c FROM ContentEntity c ORDER BY c.createdAt DESC")
    Page<ContentEntity> findRecentlyAdded(Pageable pageable);

    @Query(value = "SELECT c.* FROM contents c " +
            "JOIN student_study_groups ssg ON c.author_id = ssg.student_id " +
            "WHERE ssg.group_id IN (SELECT group_id FROM student_study_groups WHERE student_id = :studentId) " +
            "AND c.author_id != :studentId " +
            "ORDER BY c.created_at DESC", nativeQuery = true)
    Page<ContentEntity> findContentsByStudyGroupMembers(@Param("studentId") UUID studentId, Pageable pageable);

    @Query(value = "SELECT c.* FROM contents c " +
            "JOIN content_tags ct ON c.id = ct.content_id " +
            "JOIN (SELECT tag FROM content_tags WHERE content_id IN " +
            "(SELECT content_id FROM content_ratings WHERE student_id = :studentId AND rating >= 4) " +
            "GROUP BY tag) relevant_tags ON ct.tag = relevant_tags.tag " +
            "WHERE c.author_id != :studentId " +
            "AND c.id NOT IN (SELECT content_id FROM content_ratings WHERE student_id = :studentId) " +
            "GROUP BY c.id " +
            "ORDER BY COUNT(DISTINCT ct.tag) DESC, c.average_rating DESC", nativeQuery = true)
    Page<ContentEntity> recommendContentsByUserPreferences(@Param("studentId") UUID studentId, Pageable pageable);
}