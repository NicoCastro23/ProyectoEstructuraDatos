package com.plataformaEducativa.proyectoestructuradatos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plataformaEducativa.proyectoestructuradatos.dto.ModeratorDto;
import com.plataformaEducativa.proyectoestructuradatos.entity.ModeratorEntity;
import com.plataformaEducativa.proyectoestructuradatos.exception.ResourceNotFoundException;
import com.plataformaEducativa.proyectoestructuradatos.mapper.ModeratorMapper;
import com.plataformaEducativa.proyectoestructuradatos.repository.ModeratorRepository;
import com.plataformaEducativa.proyectoestructuradatos.repository.StudentConnectionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModeratorService {

    private final ModeratorRepository moderatorRepository;
    private final StudentConnectionRepository connectionRepository;
    private final ModeratorMapper moderatorMapper;
    private final PasswordEncoder passwordEncoder;

    public List<ModeratorDto> getAllModerators() {
        return moderatorRepository.findAll().stream()
                .map(moderatorMapper::entityToDto)
                .collect(Collectors.toList());
    }

    public ModeratorDto getModeratorById(UUID id) {
        ModeratorEntity moderator = moderatorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Moderator not found with id: " + id));

        return moderatorMapper.entityToDto(moderator);
    }

    public ModeratorDto getModeratorByUsername(String username) {
        ModeratorEntity moderator = moderatorRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Moderator not found with username: " + username));

        return moderatorMapper.entityToDto(moderator);
    }

    @Transactional
    public ModeratorDto updateModerator(UUID id, ModeratorDto moderatorDto) {
        ModeratorEntity moderator = moderatorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Moderator not found with id: " + id));

        // Security check - only allow users to update their own profile or higher
        // access level moderators
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isHigherAccessLevel = false;

        if (!moderator.getUsername().equals(currentUsername)) {
            ModeratorEntity currentModerator = moderatorRepository.findByUsername(currentUsername)
                    .orElseThrow(() -> new AccessDeniedException("Not authorized"));

            // Check if current moderator has higher access level
            isHigherAccessLevel = currentModerator.getAccessLevel() > moderator.getAccessLevel();

            if (!isHigherAccessLevel) {
                throw new AccessDeniedException("You do not have permission to update this moderator");
            }
        }

        moderatorMapper.updateEntityFromDto(moderatorDto, moderator);
        ModeratorEntity updatedModerator = moderatorRepository.save(moderator);

        return moderatorMapper.entityToDto(updatedModerator);
    }

    @Transactional
    public ModeratorDto updatePassword(UUID id, String currentPassword, String newPassword) {
        ModeratorEntity moderator = moderatorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Moderator not found with id: " + id));

        // Security check - only allow users to update their own password
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!moderator.getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("You are not authorized to change this moderator's password");
        }

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, moderator.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Update password
        moderator.setPassword(passwordEncoder.encode(newPassword));
        ModeratorEntity updatedModerator = moderatorRepository.save(moderator);

        return moderatorMapper.entityToDto(updatedModerator);
    }

    public List<ModeratorDto> findModeratorsByDepartment(String department) {
        return moderatorRepository.findByDepartment(department).stream()
                .map(moderatorMapper::entityToDto)
                .collect(Collectors.toList());
    }

    public List<ModeratorDto> findModeratorsBySpecialization(String specialization) {
        return moderatorRepository.findBySpecialization(specialization).stream()
                .map(moderatorMapper::entityToDto)
                .collect(Collectors.toList());
    }

    public List<ModeratorDto> findModeratorsByMinimumAccessLevel(Integer level) {
        return moderatorRepository.findByMinimumAccessLevel(level).stream()
                .map(moderatorMapper::entityToDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las conexiones más fuertes entre estudiantes
     * 
     * @param limit Número máximo de conexiones a retornar
     * @return Lista de mapas con información de conexiones
     * @throws IllegalArgumentException si el límite es inválido
     */
    public List<Map<String, Object>> getTopStudentConnections(int limit) {
        validateLimit(limit);

        List<Object[]> results = connectionRepository.findTopConnections(limit);

        return results.stream()
                .map(this::mapConnectionResult)
                .collect(Collectors.toList());
    }

    /**
     * Valida que el límite sea válido
     * 
     * @param limit Límite a validar
     * @throws IllegalArgumentException si el límite es inválido
     */
    private void validateLimit(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }
        if (limit > 100) {
            throw new IllegalArgumentException("Limit cannot exceed 100");
        }
    }

    /**
     * Mapea el resultado de la consulta a un mapa estructurado
     * 
     * @param result Array con datos de la conexión [studentA, studentB,
     *               connectionStrength]
     * @return Mapa con información estructurada de la conexión
     */
    private Map<String, Object> mapConnectionResult(Object[] result) {
        validateResultArray(result);

        return Map.of(
                "studentA", result[0],
                "studentB", result[1],
                "connectionStrength", result[2],
                "connectionType", "study-group", // Metadata adicional
                "retrievedAt", LocalDateTime.now().toString());
    }

    /**
     * Valida que el array de resultado tenga la estructura esperada
     * 
     * @param result Array a validar
     * @throws IllegalStateException si la estructura es inválida
     */
    private void validateResultArray(Object[] result) {
        if (result == null || result.length < 3) {
            throw new IllegalStateException("Invalid connection result structure");
        }

        if (result[0] == null || result[1] == null || result[2] == null) {
            throw new IllegalStateException("Connection result contains null values");
        }
    }

    public Map<String, Object> getShortestPath(UUID startStudentId, UUID endStudentId) {
        return connectionRepository.findShortestPath(startStudentId, endStudentId)
                .map(result -> Map.of(
                        "path", result[0],
                        "depth", result[1],
                        "minStrength", result[2]))
                .orElse(Map.of("error", "No path found between the students"));
    }

    public List<Map<String, Object>> getStudentConnectionsStats() {
        return connectionRepository.findConnectionsBetweenMostConnectedStudents().stream()
                .map(result -> Map.of(
                        "connection", result[0],
                        "totalConnections", result[1]))
                .collect(Collectors.toList());
    }
}
