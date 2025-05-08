package com.plataformaEducativa.proyectoestructuradatos.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("ESTUDIANTE")
@Getter
@Setter
public class EstudianteEntity extends UsuarioEntity {

    @Column(nullable = true)
    private String nivelAcademico;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "estudiante_intereses", joinColumns = @JoinColumn(name = "estudiante_id"))
    @Column(name = "interes")
    private List<String> intereses;
}
