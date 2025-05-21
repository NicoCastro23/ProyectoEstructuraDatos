package com.plataformaEducativa.proyectoestructuradatos.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "moderators")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ModeratorEntity extends UserEntity {

    @Column(name = "department")
    private String department;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "access_level")
    private Integer accessLevel;

    @Column(name = "contact_info")
    private String contactInfo;
}