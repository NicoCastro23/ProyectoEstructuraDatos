package com.plataformaEducativa.proyectoestructuradatos.controller;

import com.plataformaEducativa.proyectoestructuradatos.dto.AuthResponseDto;
import com.plataformaEducativa.proyectoestructuradatos.dto.LoginDto;
import com.plataformaEducativa.proyectoestructuradatos.dto.RegisterDto;
import com.plataformaEducativa.proyectoestructuradatos.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth") // Asegúrate de que esto sea correcto, sin "/api/v1" prefijo
@RequiredArgsConstructor
public class AuthController {

        private final AuthService authService;

        @PostMapping("/register")
        public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterDto registerDto) {
                return new ResponseEntity<>(authService.register(registerDto), HttpStatus.CREATED);
        }

        @PostMapping("/login")
        public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
                return ResponseEntity.ok(authService.login(loginDto));
        }
}