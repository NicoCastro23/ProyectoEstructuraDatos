package com.plataformaEducativa.proyectoestructuradatos.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "student_connections", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "student_a_id", "student_b_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentConnectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_a_id", nullable = false)
    private StudentEntity studentA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_b_id", nullable = false)
    private StudentEntity studentB;

    @Column(name = "connection_strength", nullable = false)
    private Integer connectionStrength = 1;

    @ElementCollection
    @CollectionTable(name = "connection_common_interests", joinColumns = @JoinColumn(name = "connection_id"))
    @Column(name = "interest")
    private Set<String> commonInterests = new HashSet<>();

    @Column(name = "last_interaction")
    private LocalDateTime lastInteraction;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
