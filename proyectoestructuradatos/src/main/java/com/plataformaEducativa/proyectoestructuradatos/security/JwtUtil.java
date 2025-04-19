package com.plataformaEducativa.proyectoestructuradatos.security;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    /**
     * Genera un token JWT para el usuario
     * 
     * @param userId ID del usuario
     * @return Token JWT generado
     */
    public String generateToken(UUID userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);

        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        return JWT.create()
                .withSubject(userId.toString())
                .withIssuedAt(now)
                .withExpiresAt(expiration)
                .sign(algorithm);
    }

    /**
     * Valida un token JWT
     * 
     * @param token Token JWT a validar
     * @return true si el token es válido, false en caso contrario
     */
    public boolean validateToken(String token) {
        try {
            // Verificar el token y asegurarse de que no esté expirado
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secretKey))
                    .build()
                    .verify(token);

            return !isTokenExpired(decodedJWT);
        } catch (JWTVerificationException e) {
            log.error("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extrae el ID del usuario del token JWT
     * 
     * @param token Token JWT
     * @return ID del usuario
     */
    public UUID extractUserId(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            String userIdString = decodedJWT.getSubject();
            return UUID.fromString(userIdString);
        } catch (Exception e) {
            log.error("Error extrayendo userId del token: {}", e.getMessage());
            throw new JWTVerificationException("Error extrayendo userId del token", e);
        }
    }

    /**
     * Verifica si un token está expirado
     * 
     * @param decodedJWT Token JWT decodificado
     * @return true si el token está expirado, false en caso contrario
     */
    private boolean isTokenExpired(DecodedJWT decodedJWT) {
        Date expiration = decodedJWT.getExpiresAt();
        return expiration.before(new Date());
    }
}