package com.plataformaEducativa.proyectoestructuradatos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plataformaEducativa.proyectoestructuradatos.dto.HelpRequestDto;
import com.plataformaEducativa.proyectoestructuradatos.entity.StudentEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.HelpRequestEntity;
import com.plataformaEducativa.proyectoestructuradatos.exception.ResourceNotFoundException;
import com.plataformaEducativa.proyectoestructuradatos.mapper.HelpRequestMapper;
import com.plataformaEducativa.proyectoestructuradatos.models.HelpRequest;
import com.plataformaEducativa.proyectoestructuradatos.models.datastructure.HelpRequestPriorityQueue.HelpRequestPriorityQueue;
import com.plataformaEducativa.proyectoestructuradatos.repository.HelpRequestRepository;
import com.plataformaEducativa.proyectoestructuradatos.repository.StudentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HelpRequestService {

    private final HelpRequestRepository helpRequestRepository;
    private final StudentRepository studentRepository;
    private final HelpRequestMapper helpRequestMapper;

    public List<HelpRequestDto> getAllHelpRequests() {
        return helpRequestRepository.findAll().stream()
                .map(helpRequestMapper::entityToDto)
                .collect(Collectors.toList());
    }

    public HelpRequestDto getHelpRequestById(UUID id) {
        HelpRequestEntity helpRequest = helpRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Help request not found with id: " + id));

        return helpRequestMapper.entityToDto(helpRequest);
    }

    @Transactional
    public HelpRequestDto createHelpRequest(HelpRequestDto helpRequestDto) {
        // Get current student as requester
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        StudentEntity requester = studentRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AccessDeniedException("Only students can create help requests"));

        // Set the requester ID
        helpRequestDto.setRequesterId(requester.getId());

        // Initialize default values
        helpRequestDto.setResolved(false);

        HelpRequestEntity helpRequest = helpRequestMapper.dtoToEntity(helpRequestDto);
        HelpRequestEntity savedHelpRequest = helpRequestRepository.save(helpRequest);

        return helpRequestMapper.entityToDto(savedHelpRequest);
    }

    @Transactional
    public HelpRequestDto updateHelpRequest(UUID id, HelpRequestDto helpRequestDto) {
        HelpRequestEntity helpRequest = helpRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Help request not found with id: " + id));

        // Security check - only requester, helper, or moderator can update
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isModerator = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR"));

        boolean isRequester = helpRequest.getRequester().getUsername().equals(currentUsername);
        boolean isHelper = helpRequest.getHelper() != null &&
                helpRequest.getHelper().getUsername().equals(currentUsername);

        if (!isRequester && !isHelper && !isModerator) {
            throw new AccessDeniedException("You are not authorized to update this help request");
        }

        helpRequestMapper.updateEntityFromDto(helpRequestDto, helpRequest);
        HelpRequestEntity updatedHelpRequest = helpRequestRepository.save(helpRequest);

        return helpRequestMapper.entityToDto(updatedHelpRequest);
    }

    @Transactional
    public void deleteHelpRequest(UUID id) {
        HelpRequestEntity helpRequest = helpRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Help request not found with id: " + id));

        // Security check - only requester or moderator can delete
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isModerator = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR"));

        boolean isRequester = helpRequest.getRequester().getUsername().equals(currentUsername);

        if (!isRequester && !isModerator) {
            throw new AccessDeniedException("You are not authorized to delete this help request");
        }

        helpRequestRepository.delete(helpRequest);
    }

    @Transactional
    public HelpRequestDto offerHelp(UUID helpRequestId) {
        HelpRequestEntity helpRequest = helpRequestRepository.findById(helpRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Help request not found with id: " + helpRequestId));

        // Check if the request is already resolved or has a helper
        if (helpRequest.isResolved()) {
            throw new IllegalStateException("This help request is already resolved");
        }

        if (helpRequest.getHelper() != null) {
            throw new IllegalStateException("This help request already has a helper");
        }

        // Get current student as helper
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        StudentEntity helper = studentRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AccessDeniedException("Only students can offer help"));

        // Cannot help your own request
        if (helpRequest.getRequester().getId().equals(helper.getId())) {
            throw new IllegalStateException("You cannot offer help on your own request");
        }

        // Set helper
        helpRequest.setHelper(helper);
        HelpRequestEntity updatedHelpRequest = helpRequestRepository.save(helpRequest);

        return helpRequestMapper.entityToDto(updatedHelpRequest);
    }

    @Transactional
    public HelpRequestDto resolveHelpRequest(UUID helpRequestId) {
        HelpRequestEntity helpRequest = helpRequestRepository.findById(helpRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Help request not found with id: " + helpRequestId));

        // Security check - only requester, helper, or moderator can resolve
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isModerator = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR"));

        boolean isRequester = helpRequest.getRequester().getUsername().equals(currentUsername);
        boolean isHelper = helpRequest.getHelper() != null &&
                helpRequest.getHelper().getUsername().equals(currentUsername);

        if (!isRequester && !isHelper && !isModerator) {
            throw new AccessDeniedException("You are not authorized to resolve this help request");
        }

        // Set as resolved
        helpRequest.setResolved(true);
        helpRequest.setResolvedAt(LocalDateTime.now());
        HelpRequestEntity updatedHelpRequest = helpRequestRepository.save(helpRequest);

        return helpRequestMapper.entityToDto(updatedHelpRequest);
    }

    public List<HelpRequestDto> getActiveHelpRequests() {
        return helpRequestRepository.findByResolvedFalseOrderByPriorityDescCreatedAtAsc().stream()
                .map(helpRequestMapper::entityToDto)
                .collect(Collectors.toList());
    }

    public List<HelpRequestDto> getHelpRequestsByTopic(String topic) {
        return helpRequestRepository.findByTopicAndResolvedFalse(topic).stream()
                .map(helpRequestMapper::entityToDto)
                .collect(Collectors.toList());
    }

    public List<HelpRequestDto> searchActiveHelpRequestsByKeyword(String keyword) {
        return helpRequestRepository.searchActiveRequestsByKeyword(keyword).stream()
                .map(helpRequestMapper::entityToDto)
                .collect(Collectors.toList());
    }

    public List<HelpRequestDto> getHelpRequestsForStudent(UUID studentId) {
        return helpRequestRepository.findRequestsMatchingStudentInterests(studentId).stream()
                .map(helpRequestMapper::entityToDto)
                .collect(Collectors.toList());
    }

    public HelpRequestPriorityQueue buildHelpRequestPriorityQueue() {
        List<HelpRequestEntity> activeRequests = helpRequestRepository
                .findByResolvedFalseOrderByPriorityDescCreatedAtAsc();
        HelpRequestPriorityQueue priorityQueue = new HelpRequestPriorityQueue();

        for (HelpRequestEntity entity : activeRequests) {
            HelpRequest model = helpRequestMapper.entityToModel(entity);
            priorityQueue.enqueue(model);
        }

        return priorityQueue;
    }

    public HelpRequestDto getNextHighestPriorityRequest() {
        HelpRequestPriorityQueue priorityQueue = buildHelpRequestPriorityQueue();

        if (priorityQueue.isEmpty()) {
            return null;
        }

        HelpRequest nextRequest = priorityQueue.peek();
        return HelpRequestDto.builder()
                .id(nextRequest.getId())
                .title(nextRequest.getTitle())
                .description(nextRequest.getDescription())
                .topic(nextRequest.getTopic())
                .priority(nextRequest.getPriority())
                .requesterId(nextRequest.getRequester().getId())
                .requesterUsername(nextRequest.getRequester().getUsername())
                .helperId(nextRequest.getHelper() != null ? nextRequest.getHelper().getId() : null)
                .helperUsername(nextRequest.getHelper() != null ? nextRequest.getHelper().getUsername() : null)
                .resolved(nextRequest.isResolved())
                .resolvedAt(nextRequest.getResolvedAt())
                .createdAt(nextRequest.getCreatedAt())
                .updatedAt(nextRequest.getUpdatedAt())
                .build();
    }
}
