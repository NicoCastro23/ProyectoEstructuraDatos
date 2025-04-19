package com.plataformaEducativa.proyectoestructuradatos.dto;

import com.plataformaEducativa.proyectoestructuradatos.enums.RoleEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDto {
    private boolean ok;
    private String token;
    private String name;
    private RoleEnum role;
    private String userType;
    private String message;
}