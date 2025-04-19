package com.plataformaEducativa.proyectoestructuradatos.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // Lista de rutas públicas que no requieren autenticación
    private final List<String> publicPaths = Arrays.asList(
            "/api/auth/**",
            "/ws/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/**");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Verificar si la ruta es pública
        if (isPublicPath(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");

        // Verificar si existe un token de autorización y tiene el formato correcto
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            try {
                // Validar el token
                if (jwtUtil.validateToken(token)) {
                    // Establecer la autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(
                            new CustomAuthentication(jwtUtil.extractUserId(token).toString()));
                    filterChain.doFilter(request, response);
                    return;
                }
            } catch (Exception e) {
                log.error("Error validando el token JWT: {}", e.getMessage());
            }
        }

        // Si llegamos aquí, el token no es válido o no existe
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"ok\": false, \"msg\": \"No autenticado\"}");
    }

    private boolean isPublicPath(String requestUri) {
        return publicPaths.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestUri));
    }
}