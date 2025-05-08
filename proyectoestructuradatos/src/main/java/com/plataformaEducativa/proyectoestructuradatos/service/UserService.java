package com.plataformaEducativa.proyectoestructuradatos.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plataformaEducativa.proyectoestructuradatos.dto.RegisterRequestDto;
import com.plataformaEducativa.proyectoestructuradatos.entity.EstudianteEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.ModeradorEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.UsuarioEntity;
import com.plataformaEducativa.proyectoestructuradatos.enums.RoleEnum;
import com.plataformaEducativa.proyectoestructuradatos.exception.AuthenticationException;
import com.plataformaEducativa.proyectoestructuradatos.exception.InvalidRoleException;
import com.plataformaEducativa.proyectoestructuradatos.exception.UserAlreadyExistsException;
import com.plataformaEducativa.proyectoestructuradatos.exception.UserNotFoundException;
import com.plataformaEducativa.proyectoestructuradatos.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Autentica a un usuario con su email y contraseña
     * 
     * @param email    Email del usuario
     * @param password Contraseña del usuario
     * @return La entidad del usuario autenticado
     * @throws AuthenticationException Si las credenciales son inválidas
     */
    public UsuarioEntity authenticateUser(String email, String password) throws AuthenticationException {
        UsuarioEntity user = usuarioRepository.findByEmail(email);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException("Credenciales inválidas");
        }

        return user;
    }

    /**
     * Registra un nuevo usuario en el sistema
     * 
     * @param registerRequest Datos de registro del usuario
     * @return La entidad del usuario creado
     * @throws InvalidRoleException       Si el rol especificado no es válido
     * @throws UserAlreadyExistsException Si el email ya está registrado
     */
    @Transactional
    public UsuarioEntity registerUser(RegisterRequestDto registerRequest) {
        // Verificar si el usuario ya existe
        if (usuarioRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("El email ya está registrado");
        }

        // Encodear la contraseña
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        UsuarioEntity newUser;

        // Crear la entidad específica según el rol
        if (registerRequest.getRole() == RoleEnum.ESTUDIANTE) {
            EstudianteEntity estudiante = new EstudianteEntity();
            estudiante.setEmail(registerRequest.getEmail());
            estudiante.setName(registerRequest.getName());
            estudiante.setPassword(encodedPassword);
            estudiante.setRole(RoleEnum.ESTUDIANTE);

            // Establecer los nuevos campos
            if (registerRequest.getNivelAcademico() != null) {
                estudiante.setNivelAcademico(registerRequest.getNivelAcademico());
            }
            if (registerRequest.getIntereses() != null) {
                estudiante.setIntereses(registerRequest.getIntereses());
            }

            newUser = usuarioRepository.save(estudiante);
        } else if (registerRequest.getRole() == RoleEnum.MODERADOR) {
            ModeradorEntity moderador = new ModeradorEntity();
            moderador.setEmail(registerRequest.getEmail());
            moderador.setName(registerRequest.getName());
            moderador.setPassword(encodedPassword);
            moderador.setRole(RoleEnum.MODERADOR);
            newUser = usuarioRepository.save(moderador);
        } else {
            throw new InvalidRoleException("Rol no válido");
        }

        return newUser;
    }

    /**
     * Obtiene un usuario por su ID
     * 
     * @param id ID del usuario
     * @return La entidad del usuario
     * @throws UserNotFoundException Si el usuario no existe
     */
    public UsuarioEntity getUserById(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + id));
    }
}