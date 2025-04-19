package com.plataformaEducativa.proyectoestructuradatos.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plataformaEducativa.proyectoestructuradatos.entity.UsuarioEntity;
import com.plataformaEducativa.proyectoestructuradatos.enums.RoleEnum;

//  Usuario tiene que ser entidad
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    // Aquí se pueden definir consultas personalizadas si es necesario
    UsuarioEntity findByEmail(String email);

    // Verificar si el correo electrónico ya existe
    boolean existsByEmail(String email);

    public Optional<UsuarioEntity> getUserById(UUID id);

    // Método para buscar usuarios por su Id
    public Optional<UsuarioEntity> findById(UUID id);

    List<UsuarioEntity> findByRole(RoleEnum role);
}
