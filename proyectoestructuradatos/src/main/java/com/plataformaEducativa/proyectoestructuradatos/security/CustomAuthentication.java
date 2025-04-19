package com.plataformaEducativa.proyectoestructuradatos.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomAuthentication implements Authentication {
    private final String token;
    private boolean authenticated = true; // Asumimos que el usuario está autenticado al usar el token

    public CustomAuthentication(String token) {
        this.token = token;
    }

    @Override
    public String getName() {
        return token; // Aquí puedes retornar el nombre del usuario si lo tienes
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Retorna las autoridades del usuario
        return null; // Implementa la lógica para obtener las autoridades si es necesario
    }

    @Override
    public Object getCredentials() {
        return token; // Retorna el token como credencial
    }

    @Override
    public Object getDetails() {
        return null; // Puedes retornar detalles adicionales del usuario si lo necesitas
    }

    @Override
    public Object getPrincipal() {
        return token; // Retorna el token o el usuario principal
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated; // Aquí puedes implementar la lógica de autenticación
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        this.authenticated = isAuthenticated; // Cambia el estado de autenticación si es necesario
    }
}
