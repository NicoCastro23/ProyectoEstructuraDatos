package com.plataformaEducativa.proyectoestructuradatos.models;

import java.util.List;
import java.util.UUID;

import com.plataformaEducativa.proyectoestructuradatos.enums.RoleEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Estudiante extends Usuario {
    private String nivelAcademico;
    private List<String> intereses;

    @Builder(builderMethodName = "estudianteBuilder")
    public Estudiante(UUID id, String email, String name, RoleEnum role, String password,
            String nivelAcademico, List<String> intereses) {
        super(id, email, name, role, password);
        this.nivelAcademico = nivelAcademico;
        this.intereses = intereses;
    }
}
