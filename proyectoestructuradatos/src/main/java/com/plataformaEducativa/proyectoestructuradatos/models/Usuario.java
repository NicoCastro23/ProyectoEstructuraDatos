package com.plataformaEducativa.proyectoestructuradatos.models;

import java.util.UUID;

import com.plataformaEducativa.proyectoestructuradatos.enums.RoleEnum;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Usuario {
    private UUID id;
    private String email;
    private String name;
    private RoleEnum role;
    private String password;
}
