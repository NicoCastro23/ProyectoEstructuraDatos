package com.plataformaEducativa.proyectoestructuradatos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;

import com.plataformaEducativa.proyectoestructuradatos.dto.AuthResponseDto;
import com.plataformaEducativa.proyectoestructuradatos.dto.LoginRequestDto;
import com.plataformaEducativa.proyectoestructuradatos.dto.RegisterRequestDto;
import com.plataformaEducativa.proyectoestructuradatos.entity.UsuarioEntity;
import com.plataformaEducativa.proyectoestructuradatos.exception.AuthenticationException;
import com.plataformaEducativa.proyectoestructuradatos.exception.InvalidRoleException;
import com.plataformaEducativa.proyectoestructuradatos.security.JwtUtil;
import com.plataformaEducativa.proyectoestructuradatos.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            UsuarioEntity userEntity = userService.authenticateUser(loginRequest.getEmail(),
                    loginRequest.getPassword());

            String token = jwtService.generateToken(userEntity.getId());

            return ResponseEntity.ok(
                    AuthResponseDto.builder()
                            .ok(true)
                            .token(token)
                            .name(userEntity.getName())
                            .role(userEntity.getRole())
                            .userType(userEntity.getClass().getSimpleName())
                            .build());

        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponseDto.builder()
                            .ok(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthResponseDto.builder()
                            .ok(false)
                            .message("Error en el proceso de autenticación: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto registerRequest) {
        try {
            UsuarioEntity newUser = userService.registerUser(registerRequest);

            String token = jwtService.generateToken(newUser.getId());

            return ResponseEntity.ok(
                    AuthResponseDto.builder()
                            .ok(true)
                            .token(token)
                            .name(newUser.getName())
                            .role(newUser.getRole())
                            .userType(newUser.getClass().getSimpleName())
                            .message("Usuario registrado exitosamente")
                            .build());

        } catch (InvalidRoleException e) {
            return ResponseEntity.badRequest()
                    .body(AuthResponseDto.builder()
                            .ok(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthResponseDto.builder()
                            .ok(false)
                            .message("Error en el proceso de registro: " + e.getMessage())
                            .build());
        }
    }
}