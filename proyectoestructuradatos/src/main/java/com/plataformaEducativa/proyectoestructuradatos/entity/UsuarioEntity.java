package com.plataformaEducativa.proyectoestructuradatos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

import com.plataformaEducativa.proyectoestructuradatos.enums.RoleEnum;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Mejor usar UUID directamente si estás usando UUIDs
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private RoleEnum role;
}
