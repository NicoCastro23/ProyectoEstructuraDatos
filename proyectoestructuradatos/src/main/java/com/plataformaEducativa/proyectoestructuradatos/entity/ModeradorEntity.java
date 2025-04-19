package com.plataformaEducativa.proyectoestructuradatos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("MODERADOR")
@Getter
@Setter
public class ModeradorEntity extends UsuarioEntity {

}
