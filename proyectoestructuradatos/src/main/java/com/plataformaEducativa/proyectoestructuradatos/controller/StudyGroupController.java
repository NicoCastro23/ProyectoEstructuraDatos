package com.plataformaEducativa.proyectoestructuradatos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.plataformaEducativa.proyectoestructuradatos.dto.StudyGroupDto;
import com.plataformaEducativa.proyectoestructuradatos.service.StudyGroupService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/study-groups")
@RequiredArgsConstructor
public class StudyGroupController {

    private final StudyGroupService studyGroupService;

    @GetMapping
    public ResponseEntity<List<StudyGroupDto>> getAllStudyGroups() {
        return ResponseEntity.ok(studyGroupService.getAllStudyGroups());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudyGroupDto> getStudyGroupById(@PathVariable UUID id) {
        return ResponseEntity.ok(studyGroupService.getStudyGroupById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudyGroupDto> createStudyGroup(@Valid @RequestBody StudyGroupDto studyGroupDto) {
        return new ResponseEntity<>(studyGroupService.createStudyGroup(studyGroupDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudyGroupDto> updateStudyGroup(
            @PathVariable UUID id,
            @Valid @RequestBody StudyGroupDto studyGroupDto) {
        return ResponseEntity.ok(studyGroupService.updateStudyGroup(id, studyGroupDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<Void> deleteStudyGroup(@PathVariable UUID id) {
        studyGroupService.deleteStudyGroup(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/join")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudyGroupDto> joinStudyGroup(@PathVariable UUID id) {
        return ResponseEntity.ok(studyGroupService.joinStudyGroup(id));
    }

    @PostMapping("/{id}/leave")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudyGroupDto> leaveStudyGroup(@PathVariable UUID id) {
        return ResponseEntity.ok(studyGroupService.leaveStudyGroup(id));
    }

    @GetMapping("/topic/{topic}")
    public ResponseEntity<List<StudyGroupDto>> findStudyGroupsByTopic(@PathVariable String topic) {
        return ResponseEntity.ok(studyGroupService.findStudyGroupsByTopic(topic));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudyGroupDto>> findStudyGroupsByStudentId(@PathVariable UUID studentId) {
        return ResponseEntity.ok(studyGroupService.findStudyGroupsByStudentId(studentId));
    }

    @GetMapping("/available")
    public ResponseEntity<List<StudyGroupDto>> findAvailableStudyGroups() {
        return ResponseEntity.ok(studyGroupService.findAvailableStudyGroups());
    }

    @GetMapping("/recommendations/{studentId}")
    public ResponseEntity<List<StudyGroupDto>> recommendStudyGroupsByInterests(@PathVariable UUID studentId) {
        return ResponseEntity.ok(studyGroupService.recommendStudyGroupsByInterests(studentId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<StudyGroupDto>> findActiveGroupsWithMinMembers(
            @RequestParam(defaultValue = "2") int minMembers) {
        return ResponseEntity.ok(studyGroupService.findActiveGroupsWithMinMembers(minMembers));
    }

    @PostMapping("/auto-generate")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<StudyGroupDto> createStudyGroupAutomatically(
            @RequestBody Set<String> topics,
            @RequestParam(required = false) Integer maxCapacity) {
        return new ResponseEntity<>(
                studyGroupService.createStudyGroupAutomatically(topics, maxCapacity),
                HttpStatus.CREATED);
    }
}