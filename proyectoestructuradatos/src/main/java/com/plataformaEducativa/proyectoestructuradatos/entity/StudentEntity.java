package com.plataformaEducativa.proyectoestructuradatos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StudentEntity extends UserEntity {

    @ElementCollection
    @CollectionTable(name = "student_interests", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "interest")
    private Set<String> academicInterests = new HashSet<>();

    @Column(name = "study_field")
    private String fieldOfStudy;

    @Column(name = "education_level")
    private String educationLevel;

    @Column(name = "profile_bio", length = 500)
    private String bio;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ContentEntity> publishedContents = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "student_study_groups", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<StudyGroupEntity> studyGroups = new HashSet<>();

    @OneToMany(mappedBy = "requester", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HelpRequestEntity> helpRequests = new HashSet<>();
}
