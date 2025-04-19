package com.plataformaEducativa.proyectoestructuradatos.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("ESTUDIANTE")
@Getter
@Setter
public class EstudianteEntity extends UsuarioEntity {

}
