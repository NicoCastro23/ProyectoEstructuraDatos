package com.plataformaEducativa.proyectoestructuradatos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plataformaEducativa.proyectoestructuradatos.models.Content;
import com.plataformaEducativa.proyectoestructuradatos.dto.ContentDto;
import com.plataformaEducativa.proyectoestructuradatos.entity.ContentEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.ContentRatingEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.StudentEntity;
import com.plataformaEducativa.proyectoestructuradatos.exception.ResourceNotFoundException;
import com.plataformaEducativa.proyectoestructuradatos.mapper.ContentMapper;
import com.plataformaEducativa.proyectoestructuradatos.models.datastructure.BinarySearchTree.ContentBinarySearchTree;
import com.plataformaEducativa.proyectoestructuradatos.repository.ContentRatingRepository;
import com.plataformaEducativa.proyectoestructuradatos.repository.ContentRepository;
import com.plataformaEducativa.proyectoestructuradatos.repository.StudentRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;
    private final ContentRatingRepository ratingRepository;
    private final StudentRepository studentRepository;
    private final ContentMapper contentMapper;

    public Page<ContentDto> getAllContents(Pageable pageable) {
        return contentRepository.findAll(pageable)
                .map(contentMapper::entityToDto);
    }

    public ContentDto getContentById(UUID id) {
        ContentEntity content = contentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));

        // Increment view count
        content.setViewCount(content.getViewCount() + 1);
        contentRepository.save(content);

        return contentMapper.entityToDto(content);
    }

    @Transactional
    public ContentDto createContent(ContentDto contentDto) {
        // Get current user as author
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        StudentEntity author = studentRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AccessDeniedException("Only students can create content"));

        // Set the author ID
        contentDto.setAuthorId(author.getId());

        // Initialize ratings and views
        contentDto.setAverageRating(0.0);
        contentDto.setRatingCount(0);
        contentDto.setViewCount(0);

        ContentEntity content = contentMapper.dtoToEntity(contentDto);
        ContentEntity savedContent = contentRepository.save(content);

        return contentMapper.entityToDto(savedContent);
    }

    @Transactional
    public ContentDto updateContent(UUID id, ContentDto contentDto) {
        ContentEntity content = contentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));

        // Security check - only author or moderator can update content
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isModerator = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR"));

        if (!content.getAuthor().getUsername().equals(currentUsername) && !isModerator) {
            throw new AccessDeniedException("You are not authorized to update this content");
        }

        contentMapper.updateEntityFromDto(contentDto, content);
        ContentEntity updatedContent = contentRepository.save(content);

        return contentMapper.entityToDto(updatedContent);
    }

    @Transactional
    public void deleteContent(UUID id) {
        ContentEntity content = contentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));

        // Security check - only author or moderator can delete content
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isModerator = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR"));

        if (!content.getAuthor().getUsername().equals(currentUsername) && !isModerator) {
            throw new AccessDeniedException("You are not authorized to delete this content");
        }

        contentRepository.delete(content);
    }

    @Transactional
    public ContentDto rateContent(UUID contentId, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        ContentEntity content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + contentId));

        // Get current user
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        StudentEntity student = studentRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AccessDeniedException("Only students can rate content"));

        // Check if user already rated this content
        ContentRatingEntity existingRating = ratingRepository
                .findByContentIdAndStudentId(contentId, student.getId())
                .orElse(null);

        // Update or create rating
        if (existingRating != null) {
            existingRating.setRating(rating);
            existingRating.setComment(comment);
            ratingRepository.save(existingRating);
        } else {
            ContentRatingEntity newRating = ContentRatingEntity.builder()
                    .content(content)
                    .student(student)
                    .rating(rating)
                    .comment(comment)
                    .build();
            ratingRepository.save(newRating);
        }

        // Update content average rating
        Double averageRating = ratingRepository.calculateAverageRating(contentId);
        Integer ratingCount = ratingRepository.countRatingsByContentId(contentId);

        content.setAverageRating(averageRating != null ? averageRating : 0.0);
        content.setRatingCount(ratingCount != null ? ratingCount : 0);
        ContentEntity updatedContent = contentRepository.save(content);

        return contentMapper.entityToDto(updatedContent);
    }

    public Page<ContentDto> getContentsByAuthor(UUID authorId, Pageable pageable) {
        return contentRepository.findByAuthorId(authorId, pageable)
                .map(contentMapper::entityToDto);
    }

    public Page<ContentDto> searchContentsByKeyword(String keyword, Pageable pageable) {
        return contentRepository.searchByKeyword(keyword, pageable)
                .map(contentMapper::entityToDto);
    }

    public Page<ContentDto> getContentsByTag(String tag, Pageable pageable) {
        return contentRepository.findByTag(tag, pageable)
                .map(contentMapper::entityToDto);
    }

    public Page<ContentDto> getTopRatedContents(Pageable pageable) {
        return contentRepository.findTopRated(pageable)
                .map(contentMapper::entityToDto);
    }

    public Page<ContentDto> getMostViewedContents(Pageable pageable) {
        return contentRepository.findMostViewed(pageable)
                .map(contentMapper::entityToDto);
    }

    public Page<ContentDto> getRecentContents(Pageable pageable) {
        return contentRepository.findRecentlyAdded(pageable)
                .map(contentMapper::entityToDto);
    }

    public Page<ContentDto> getContentsByStudyGroupMembers(UUID studentId, Pageable pageable) {
        return contentRepository.findContentsByStudyGroupMembers(studentId, pageable)
                .map(contentMapper::entityToDto);
    }

    public Page<ContentDto> recommendContentsByPreferences(UUID studentId, Pageable pageable) {
        return contentRepository.recommendContentsByUserPreferences(studentId, pageable)
                .map(contentMapper::entityToDto);
    }

    public ContentBinarySearchTree buildContentSearchTree() {
        List<ContentEntity> allContents = contentRepository.findAll();
        ContentBinarySearchTree tree = new ContentBinarySearchTree();

        for (ContentEntity entity : allContents) {
            Content content = contentMapper.entityToModel(entity);
            tree.insert(content);
        }

        return tree;
    }

    public List<ContentDto> searchContentsByTitle(String title) {
        ContentBinarySearchTree tree = buildContentSearchTree();
        Content content = tree.search(title);

        return content != null ? List.of(ContentDto.builder()
                .id(content.getId())
                .title(content.getTitle())
                .description(content.getDescription())
                .contentUrl(content.getContentUrl())
                .contentType(content.getContentType())
                .tags(content.getTags())
                .authorUsername(content.getAuthor().getUsername())
                .averageRating(content.getAverageRating())
                .ratingCount(content.getRatingCount())
                .viewCount(content.getViewCount())
                .createdAt(content.getCreatedAt())
                .updatedAt(content.getUpdatedAt())
                .build())
                : List.of();
    }
}