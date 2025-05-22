package com.plataformaEducativa.proyectoestructuradatos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.plataformaEducativa.proyectoestructuradatos.dto.HelpRequestDto;
import com.plataformaEducativa.proyectoestructuradatos.service.HelpRequestService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/help-requests")
@RequiredArgsConstructor
public class HelpRequestController {

    private final HelpRequestService helpRequestService;

    @GetMapping
    public ResponseEntity<List<HelpRequestDto>> getAllHelpRequests() {
        return ResponseEntity.ok(helpRequestService.getAllHelpRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HelpRequestDto> getHelpRequestById(@PathVariable UUID id) {
        return ResponseEntity.ok(helpRequestService.getHelpRequestById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<HelpRequestDto> createHelpRequest(@Valid @RequestBody HelpRequestDto helpRequestDto) {
        return new ResponseEntity<>(helpRequestService.createHelpRequest(helpRequestDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HelpRequestDto> updateHelpRequest(
            @PathVariable UUID id,
            @Valid @RequestBody HelpRequestDto helpRequestDto) {
        return ResponseEntity.ok(helpRequestService.updateHelpRequest(id, helpRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHelpRequest(@PathVariable UUID id) {
        helpRequestService.deleteHelpRequest(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/offer-help")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<HelpRequestDto> offerHelp(@PathVariable UUID id) {
        return ResponseEntity.ok(helpRequestService.offerHelp(id));
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<HelpRequestDto> resolveHelpRequest(@PathVariable UUID id) {
        return ResponseEntity.ok(helpRequestService.resolveHelpRequest(id));
    }

    @GetMapping("/active")
    public ResponseEntity<List<HelpRequestDto>> getActiveHelpRequests() {
        return ResponseEntity.ok(helpRequestService.getActiveHelpRequests());
    }

    @GetMapping("/topic/{topic}")
    public ResponseEntity<List<HelpRequestDto>> getHelpRequestsByTopic(@PathVariable String topic) {
        return ResponseEntity.ok(helpRequestService.getHelpRequestsByTopic(topic));
    }

    @GetMapping("/search")
    public ResponseEntity<List<HelpRequestDto>> searchActiveHelpRequestsByKeyword(@RequestParam String keyword) {
        return ResponseEntity.ok(helpRequestService.searchActiveHelpRequestsByKeyword(keyword));
    }

    /**
     * Obtiene todas las solicitudes de ayuda creadas por un estudiante específico
     */
    @GetMapping("/created-by/{studentId}")
    public ResponseEntity<List<HelpRequestDto>> getHelpRequestsCreatedByStudent(@PathVariable UUID studentId) {
        return ResponseEntity.ok(helpRequestService.getHelpRequestsForStudent(studentId));
    }

    /**
     * Obtiene solicitudes de ayuda recomendadas basadas en los intereses del
     * estudiante
     */
    @GetMapping("/recommended-for/{studentId}")
    public ResponseEntity<List<HelpRequestDto>> getRecommendedHelpRequestsForStudent(@PathVariable UUID studentId) {
        return ResponseEntity.ok(helpRequestService.getRecommendedHelpRequestsForStudent(studentId));
    }

    @GetMapping("/next-priority")
    public ResponseEntity<HelpRequestDto> getNextHighestPriorityRequest() {
        HelpRequestDto request = helpRequestService.getNextHighestPriorityRequest();
        if (request == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(request);
    }
}
