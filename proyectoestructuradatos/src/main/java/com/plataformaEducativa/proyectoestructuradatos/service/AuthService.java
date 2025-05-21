package com.plataformaEducativa.proyectoestructuradatos.service;

import com.plataformaEducativa.proyectoestructuradatos.dto.AuthResponseDto;
import com.plataformaEducativa.proyectoestructuradatos.dto.LoginDto;
import com.plataformaEducativa.proyectoestructuradatos.dto.RegisterDto;
import com.plataformaEducativa.proyectoestructuradatos.entity.ModeratorEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.StudentEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.UserEntity;
import com.plataformaEducativa.proyectoestructuradatos.enums.UserRole;
import com.plataformaEducativa.proyectoestructuradatos.exception.ResourceAlreadyExistsException;
import com.plataformaEducativa.proyectoestructuradatos.mapper.ModeratorMapper;
import com.plataformaEducativa.proyectoestructuradatos.mapper.StudentMapper;
import com.plataformaEducativa.proyectoestructuradatos.repository.ModeratorRepository;
import com.plataformaEducativa.proyectoestructuradatos.repository.StudentRepository;
import com.plataformaEducativa.proyectoestructuradatos.repository.UserRepository;
import com.plataformaEducativa.proyectoestructuradatos.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final ModeratorRepository moderatorRepository;
    private final StudentMapper studentMapper;
    private final ModeratorMapper moderatorMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @Transactional
    public AuthResponseDto register(RegisterDto registerDto) {
        // Validar existencia de usuario y email
        validateRegistrationData(registerDto);

        // Guardar contraseña original para autenticación posterior
        String rawPassword = registerDto.getPassword();

        // Codificar la contraseña para almacenamiento seguro
        String encodedPassword = passwordEncoder.encode(rawPassword);
        registerDto.setPassword(encodedPassword);

        // Crear y persistir la entidad de usuario según su rol
        UserEntity savedUser = persistUserByRole(registerDto);

        // Autenticar al usuario recién creado y generar token JWT
        return authenticateUserAndGenerateToken(registerDto.getUsername(), rawPassword, savedUser);
    }

    /**
     * Valida que el nombre de usuario y email no existan previamente
     * 
     * @param registerDto DTO con datos de registro
     * @throws ResourceAlreadyExistsException si el username o email ya están en uso
     */
    private void validateRegistrationData(RegisterDto registerDto) {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new ResourceAlreadyExistsException("Username already taken");
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already in use");
        }
    }

    /**
     * Crea y persiste un usuario según su rol
     * 
     * @param registerDto DTO con datos de registro
     * @return La entidad de usuario persistida
     * @throws IllegalArgumentException si el rol no es válido
     */
    private UserEntity persistUserByRole(RegisterDto registerDto) {
        if (registerDto.getRole() == UserRole.STUDENT) {
            StudentEntity student = studentMapper.registerDtoToEntity(registerDto);
            return studentRepository.save(student);
        } else if (registerDto.getRole() == UserRole.MODERATOR) {
            ModeratorEntity moderator = moderatorMapper.registerDtoToEntity(registerDto);
            return moderatorRepository.save(moderator);
        } else {
            throw new IllegalArgumentException("Invalid user role: " + registerDto.getRole());
        }
    }

    /**
     * Autentica al usuario recién creado y genera un token JWT
     * 
     * @param username    Nombre de usuario
     * @param rawPassword Contraseña sin codificar
     * @param savedUser   Entidad de usuario persistida
     * @return DTO de respuesta con token JWT y datos del usuario
     */
    private AuthResponseDto authenticateUserAndGenerateToken(String username, String rawPassword,
            UserEntity savedUser) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, rawPassword));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtTokenUtil.generateToken(userDetails);

        return AuthResponseDto.builder()
                .token(jwt)
                .tokenType("Bearer")
                .username(savedUser.getUsername())
                .role(savedUser.getRole().name())
                .build();
    }

    public AuthResponseDto login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(),
                        loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtTokenUtil.generateToken(userDetails);
        String role = authentication.getAuthorities().iterator().next().getAuthority()
                .replace("ROLE_", "");

        return AuthResponseDto.builder()
                .token(jwt)
                .tokenType("Bearer")
                .username(userDetails.getUsername())
                .role(role)
                .build();
    }
}