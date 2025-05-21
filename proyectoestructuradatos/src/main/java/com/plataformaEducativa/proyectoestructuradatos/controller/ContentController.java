package com.plataformaEducativa.proyectoestructuradatos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.plataformaEducativa.proyectoestructuradatos.dto.ContentDto;
import com.plataformaEducativa.proyectoestructuradatos.service.ContentService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/contents")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @GetMapping
    public ResponseEntity<Page<ContentDto>> getAllContents(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(contentService.getAllContents(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContentDto> getContentById(@PathVariable UUID id) {
        return ResponseEntity.ok(contentService.getContentById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ContentDto> createContent(@Valid @RequestBody ContentDto contentDto) {
        return new ResponseEntity<>(contentService.createContent(contentDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContentDto> updateContent(
            @PathVariable UUID id,
            @Valid @RequestBody ContentDto contentDto) {
        return ResponseEntity.ok(contentService.updateContent(id, contentDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable UUID id) {
        contentService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/rate")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ContentDto> rateContent(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> ratingData) {

        int rating = (int) ratingData.get("rating");
        String comment = (String) ratingData.get("comment");

        return ResponseEntity.ok(contentService.rateContent(id, rating, comment));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<Page<ContentDto>> getContentsByAuthor(
            @PathVariable UUID authorId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(contentService.getContentsByAuthor(authorId, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ContentDto>> searchContentsByKeyword(
            @RequestParam String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(contentService.searchContentsByKeyword(keyword, pageable));
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<Page<ContentDto>> getContentsByTag(
            @PathVariable String tag,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(contentService.getContentsByTag(tag, pageable));
    }

    @GetMapping("/top-rated")
    public ResponseEntity<Page<ContentDto>> getTopRatedContents(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(contentService.getTopRatedContents(pageable));
    }

    @GetMapping("/most-viewed")
    public ResponseEntity<Page<ContentDto>> getMostViewedContents(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(contentService.getMostViewedContents(pageable));
    }

    @GetMapping("/recent")
    public ResponseEntity<Page<ContentDto>> getRecentContents(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(contentService.getRecentContents(pageable));
    }

    @GetMapping("/study-group-members/{studentId}")
    public ResponseEntity<Page<ContentDto>> getContentsByStudyGroupMembers(
            @PathVariable UUID studentId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(contentService.getContentsByStudyGroupMembers(studentId, pageable));
    }

    @GetMapping("/recommendations/{studentId}")
    public ResponseEntity<Page<ContentDto>> recommendContentsByPreferences(
            @PathVariable UUID studentId,
            @PageableDefault(size = 10, sort = "averageRating", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(contentService.recommendContentsByPreferences(studentId, pageable));
    }

    @GetMapping("/title-search/{title}")
    public ResponseEntity<List<ContentDto>> searchContentsByTitle(@PathVariable String title) {
        return ResponseEntity.ok(contentService.searchContentsByTitle(title));
    }
}