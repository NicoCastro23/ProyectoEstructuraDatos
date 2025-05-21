package com.plataformaEducativa.proyectoestructuradatos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.plataformaEducativa.proyectoestructuradatos.dto.ModeratorDto;
import com.plataformaEducativa.proyectoestructuradatos.models.Student;
import com.plataformaEducativa.proyectoestructuradatos.service.ModeratorService;
import com.plataformaEducativa.proyectoestructuradatos.service.StudentConnectionService;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/moderators")
@PreAuthorize("hasRole('MODERATOR')")
@RequiredArgsConstructor
public class ModeratorController {

    private final ModeratorService moderatorService;
    private final StudentConnectionService connectionService;

    @GetMapping
    public ResponseEntity<List<ModeratorDto>> getAllModerators() {
        return ResponseEntity.ok(moderatorService.getAllModerators());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModeratorDto> getModeratorById(@PathVariable UUID id) {
        return ResponseEntity.ok(moderatorService.getModeratorById(id));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ModeratorDto> getModeratorByUsername(@PathVariable String username) {
        return ResponseEntity.ok(moderatorService.getModeratorByUsername(username));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModeratorDto> updateModerator(
            @PathVariable UUID id,
            @Valid @RequestBody ModeratorDto moderatorDto) {
        return ResponseEntity.ok(moderatorService.updateModerator(id, moderatorDto));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<ModeratorDto> updatePassword(
            @PathVariable UUID id,
            @RequestBody Map<String, String> passwordData) {

        String currentPassword = passwordData.get("currentPassword");
        String newPassword = passwordData.get("newPassword");

        if (currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(moderatorService.updatePassword(id, currentPassword, newPassword));
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<List<ModeratorDto>> findModeratorsByDepartment(@PathVariable String department) {
        return ResponseEntity.ok(moderatorService.findModeratorsByDepartment(department));
    }

    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<ModeratorDto>> findModeratorsBySpecialization(
            @PathVariable String specialization) {
        return ResponseEntity.ok(moderatorService.findModeratorsBySpecialization(specialization));
    }

    @GetMapping("/access-level/{level}")
    public ResponseEntity<List<ModeratorDto>> findModeratorsByMinimumAccessLevel(@PathVariable Integer level) {
        return ResponseEntity.ok(moderatorService.findModeratorsByMinimumAccessLevel(level));
    }

    // Analytics endpoints

    @GetMapping("/analytics/top-connections")
    public ResponseEntity<List<Map<String, Object>>> getTopStudentConnections(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(moderatorService.getTopStudentConnections(limit));
    }

    @GetMapping("/analytics/path")
    public ResponseEntity<Map<String, Object>> getShortestPath(
            @RequestParam UUID startId, @RequestParam UUID endId) {
        return ResponseEntity.ok(moderatorService.getShortestPath(startId, endId));
    }

    @GetMapping("/analytics/connection-stats")
    public ResponseEntity<List<Map<String, Object>>> getStudentConnectionsStats() {
        return ResponseEntity.ok(moderatorService.getStudentConnectionsStats());
    }

    @GetMapping("/analytics/most-connected")
    public ResponseEntity<List<Map<String, Object>>> getMostConnectedStudents(
            @RequestParam(defaultValue = "10") int limit) {
        List<Student> students = connectionService.getMostConnectedStudents(limit);

        List<Map<String, Object>> result = students.stream()
                .map(s -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", s.getId());
                    map.put("username", s.getUsername());
                    map.put("fullName", s.getFullName());
                    map.put("connectionCount", s.getConnectionCount());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/analytics/communities")
    public ResponseEntity<List<List<Map<String, Object>>>> detectCommunities() {
        List<Set<Student>> communities = connectionService.detectCommunities();

        List<List<Map<String, Object>>> result = new ArrayList<>();
        for (Set<Student> community : communities) {
            List<Map<String, Object>> communityData = new ArrayList<>();
            for (Student s : community) {
                Map<String, Object> studentData = new HashMap<>();
                studentData.put("id", s.getId());
                studentData.put("username", s.getUsername());
                studentData.put("fullName", s.getFullName());
                communityData.add(studentData);
            }
            result.add(communityData);
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/analytics/generate-connections")
    public ResponseEntity<Map<String, String>> generateConnections(@RequestParam String type) {
        switch (type) {
            case "study-groups":
                connectionService.generateStudyPartnerConnections();
                break;
            case "content":
                connectionService.generateSimilarContentConnections();
                break;
            default:
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid connection generation type");
                return ResponseEntity.badRequest().body(errorResponse);
        }

        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("message", "Connections generated successfully");
        return ResponseEntity.ok(successResponse);
    }
}