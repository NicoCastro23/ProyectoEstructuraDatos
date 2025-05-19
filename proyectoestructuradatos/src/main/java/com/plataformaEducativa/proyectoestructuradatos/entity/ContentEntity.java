package com.plataformaEducativa.proyectoestructuradatos.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "contents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ContentEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String description;
    
    @Column(name = "content_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType type;
    
    @Column(name = "resource_url")
    private String resourceUrl;
    
    @Column(columnDefinition = "TEXT")
    private String body;
    
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    
    @ElementCollection
    @CollectionTable(name = "content_tags", joinColumns = @JoinColumn(name = "content_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    
    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ContentRating> ratings = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        lastUpdate = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdate = LocalDateTime.now();
    }
    
    public Double getAverageRating() {
        if (ratings.isEmpty()) {
            return 0.0;
        }
        
        return ratings.stream()
            .mapToDouble(ContentRating::getValue)
            .average()
            .orElse(0.0);
    }
    
    public enum ContentType {
        DOCUMENT, VIDEO, LINK, ARTICLE, PRESENTATION, CODE
    }
}