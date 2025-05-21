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

    // Analytics methods for moderators

    public List<Map<String, Object>> getTopStudentConnections(int limit) {
        return connectionRepository.findTopConnections(limit).stream()
                .map(result -> Map.of(
                        "studentA", result[0],
                        "studentB", result[1],
                        "connectionStrength", result[2]))
                .collect(Collectors.toList());
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
