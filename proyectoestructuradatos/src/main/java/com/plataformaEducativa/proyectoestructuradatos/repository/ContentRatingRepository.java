package com.plataformaEducativa.proyectoestructuradatos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.plataformaEducativa.proyectoestructuradatos.entity.ContentRatingEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContentRatingRepository extends JpaRepository<ContentRatingEntity, UUID> {

    List<ContentRatingEntity> findByContentId(UUID contentId);

    List<ContentRatingEntity> findByStudentId(UUID studentId);

    Optional<ContentRatingEntity> findByContentIdAndStudentId(UUID contentId, UUID studentId);

    @Query("SELECT AVG(r.rating) FROM ContentRatingEntity r WHERE r.content.id = :contentId")
    Double calculateAverageRating(@Param("contentId") UUID contentId);

    @Query("SELECT COUNT(r) FROM ContentRatingEntity r WHERE r.content.id = :contentId")
    Integer countRatingsByContentId(@Param("contentId") UUID contentId);

    @Query(value = "SELECT cr.* FROM content_ratings cr " +
            "JOIN contents c ON cr.content_id = c.id " +
            "WHERE c.author_id = :authorId " +
            "ORDER BY cr.created_at DESC", nativeQuery = true)
    List<ContentRatingEntity> findRatingsForAuthorContent(@Param("authorId") UUID authorId);
}
