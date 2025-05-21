package com.plataformaEducativa.proyectoestructuradatos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.plataformaEducativa.proyectoestructuradatos.dto.StudentDto;
import com.plataformaEducativa.proyectoestructuradatos.service.StudentConnectionService;
import com.plataformaEducativa.proyectoestructuradatos.service.StudentService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final StudentConnectionService connectionService;

    @GetMapping
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable UUID id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<StudentDto> getStudentByUsername(@PathVariable String username) {
        return ResponseEntity.ok(studentService.getStudentByUsername(username));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDto> updateStudent(
            @PathVariable UUID id,
            @Valid @RequestBody StudentDto studentDto) {
        return ResponseEntity.ok(studentService.updateStudent(id, studentDto));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<StudentDto> updatePassword(
            @PathVariable UUID id,
            @RequestBody Map<String, String> passwordData) {

        String currentPassword = passwordData.get("currentPassword");
        String newPassword = passwordData.get("newPassword");

        if (currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(studentService.updatePassword(id, currentPassword, newPassword));
    }

    @GetMapping("/interests/{interest}")
    public ResponseEntity<List<StudentDto>> findStudentsByInterest(@PathVariable String interest) {
        return ResponseEntity.ok(studentService.findStudentsByInterest(interest));
    }

    @GetMapping("/fields/{field}")
    public ResponseEntity<List<StudentDto>> findStudentsByFieldOfStudy(@PathVariable String field) {
        return ResponseEntity.ok(studentService.findStudentsByFieldOfStudy(field));
    }

    @GetMapping("/groups/{groupId}")
    public ResponseEntity<List<StudentDto>> findStudentsByStudyGroup(@PathVariable UUID groupId) {
        return ResponseEntity.ok(studentService.findStudentsByStudyGroup(groupId));
    }

    @GetMapping("/common-groups/{studentId}")
    public ResponseEntity<List<StudentDto>> findStudentsWithCommonGroups(@PathVariable UUID studentId) {
        return ResponseEntity.ok(studentService.findStudentsWithCommonGroups(studentId));
    }

    @GetMapping("/all-interests")
    public ResponseEntity<Set<String>> getAllAcademicInterests() {
        return ResponseEntity.ok(studentService.getAllAcademicInterests());
    }

    @GetMapping("/all-fields")
    public ResponseEntity<Set<String>> getAllFieldsOfStudy() {
        return ResponseEntity.ok(studentService.getAllFieldsOfStudy());
    }

    @GetMapping("/{id}/connections")
    public ResponseEntity<List<Map<String, Object>>> getStudentConnections(@PathVariable UUID id) {
        return ResponseEntity.ok(connectionService.getStudentConnections(id).stream()
                .map(conn -> Map.of(
                        "id", conn.getId(),
                        "studentA", conn.getStudentA().getUsername(),
                        "studentB", conn.getStudentB().getUsername(),
                        "strength", conn.getConnectionStrength(),
                        "commonInterests", conn.getCommonInterests(),
                        "lastInteraction", conn.getLastInteraction()))
                .toList());
    }

    @GetMapping("/{id}/recommendations")
    public ResponseEntity<Map<UUID, Integer>> getStudentRecommendations(@PathVariable UUID id) {
        return ResponseEntity.ok(connectionService.getStudentRecommendations(id));
    }

    @PostMapping("/connections")
    public ResponseEntity<Map<String, Object>> createConnection(
            @RequestBody Map<String, Object> connectionData) {

        UUID studentIdA = UUID.fromString((String) connectionData.get("studentIdA"));
        UUID studentIdB = UUID.fromString((String) connectionData.get("studentIdB"));
        @SuppressWarnings("unchecked")
        Set<String> commonInterests = (Set<String>) connectionData.get("commonInterests");

        if (commonInterests == null) {
            commonInterests = Set.of();
        }

        var connection = connectionService.createConnection(studentIdA, studentIdB, commonInterests);

        return ResponseEntity.ok(Map.of(
                "id", connection.getId(),
                "studentA", connection.getStudentA().getUsername(),
                "studentB", connection.getStudentB().getUsername(),
                "strength", connection.getConnectionStrength(),
                "commonInterests", connection.getCommonInterests(),
                "lastInteraction", connection.getLastInteraction()));
    }

    @PutMapping("/connections")
    public ResponseEntity<Map<String, Object>> updateConnection(
            @RequestBody Map<String, Object> connectionData) {

        UUID studentIdA = UUID.fromString((String) connectionData.get("studentIdA"));
        UUID studentIdB = UUID.fromString((String) connectionData.get("studentIdB"));
        Integer strengthChange = (Integer) connectionData.get("strengthChange");

        var connection = connectionService.updateConnection(studentIdA, studentIdB, strengthChange);

        return ResponseEntity.ok(Map.of(
                "id", connection.getId(),
                "studentA", connection.getStudentA().getUsername(),
                "studentB", connection.getStudentB().getUsername(),
                "strength", connection.getConnectionStrength(),
                "commonInterests", connection.getCommonInterests(),
                "lastInteraction", connection.getLastInteraction()));
    }

    @DeleteMapping("/connections")
    public ResponseEntity<Void> deleteConnection(@RequestBody Map<String, String> connectionData) {
        UUID studentIdA = UUID.fromString(connectionData.get("studentIdA"));
        UUID studentIdB = UUID.fromString(connectionData.get("studentIdB"));

        connectionService.deleteConnection(studentIdA, studentIdB);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/path")
    public ResponseEntity<List<Map<String, Object>>> findShortestPath(
            @RequestParam UUID startId, @RequestParam UUID endId) {
        return ResponseEntity.ok(connectionService.findShortestPath(startId, endId));
    }
}