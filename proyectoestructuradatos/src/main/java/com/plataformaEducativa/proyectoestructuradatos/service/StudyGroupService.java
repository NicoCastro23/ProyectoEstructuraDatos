package com.plataformaEducativa.proyectoestructuradatos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plataformaEducativa.proyectoestructuradatos.dto.StudyGroupDto;
import com.plataformaEducativa.proyectoestructuradatos.entity.StudentEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.StudyGroupEntity;
import com.plataformaEducativa.proyectoestructuradatos.exception.ResourceNotFoundException;
import com.plataformaEducativa.proyectoestructuradatos.mapper.StudyGroupMapper;
import com.plataformaEducativa.proyectoestructuradatos.repository.StudentRepository;
import com.plataformaEducativa.proyectoestructuradatos.repository.StudyGroupRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final StudentRepository studentRepository;
    private final StudyGroupMapper studyGroupMapper;

    public List<StudyGroupDto> getAllStudyGroups() {
        return studyGroupRepository.findAll().stream()
                .map(studyGroupMapper::entityToDto)
                .collect(Collectors.toList());
    }

    public StudyGroupDto getStudyGroupById(UUID id) {
        StudyGroupEntity group = studyGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Study group not found with id: " + id));

        return studyGroupMapper.entityToDto(group);
    }

    @Transactional
    public StudyGroupDto createStudyGroup(StudyGroupDto studyGroupDto) {
        StudyGroupEntity group = studyGroupMapper.dtoToEntity(studyGroupDto);

        // Add current user as a member if not already included
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        StudentEntity currentStudent = studentRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AccessDeniedException("Only students can create study groups"));

        if (group.getMembers() == null) {
            group.setMembers(new HashSet<>());
        }

        if (group.getMembers().stream().noneMatch(m -> m.getId().equals(currentStudent.getId()))) {
            group.getMembers().add(currentStudent);
        }

        // Set default values
        group.setActive(true);

        StudyGroupEntity savedGroup = studyGroupRepository.save(group);

        return studyGroupMapper.entityToDto(savedGroup);
    }

    @Transactional
    public StudyGroupDto updateStudyGroup(UUID id, StudyGroupDto studyGroupDto) {
        StudyGroupEntity group = studyGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Study group not found with id: " + id));

        // Security check - only members or moderators can update the group
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isModerator = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR"));

        boolean isMember = group.getMembers().stream()
                .anyMatch(m -> m.getUsername().equals(currentUsername));

        if (!isMember && !isModerator) {
            throw new AccessDeniedException("You are not authorized to update this study group");
        }

        studyGroupMapper.updateEntityFromDto(studyGroupDto, group);
        StudyGroupEntity updatedGroup = studyGroupRepository.save(group);

        return studyGroupMapper.entityToDto(updatedGroup);
    }

    @Transactional
    public void deleteStudyGroup(UUID id) {
        StudyGroupEntity group = studyGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Study group not found with id: " + id));

        // Security check - only moderators can delete study groups
        boolean isModerator = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR"));

        if (!isModerator) {
            throw new AccessDeniedException("You are not authorized to delete study groups");
        }

        studyGroupRepository.delete(group);
    }

    @Transactional
    public StudyGroupDto joinStudyGroup(UUID groupId) {
        StudyGroupEntity group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Study group not found with id: " + groupId));

        // Check if group is active and has capacity
        if (!group.isActive()) {
            throw new IllegalStateException("This study group is no longer active");
        }

        if (group.getMaxCapacity() != null && group.getMembers().size() >= group.getMaxCapacity()) {
            throw new IllegalStateException("This study group has reached its maximum capacity");
        }

        // Get current student
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        StudentEntity student = studentRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AccessDeniedException("Only students can join study groups"));

        // Check if student is already a member
        if (group.getMembers().stream().anyMatch(m -> m.getId().equals(student.getId()))) {
            throw new IllegalStateException("You are already a member of this study group");
        }

        // Add student to the group
        group.getMembers().add(student);
        StudyGroupEntity updatedGroup = studyGroupRepository.save(group);

        return studyGroupMapper.entityToDto(updatedGroup);
    }

    @Transactional
    public StudyGroupDto leaveStudyGroup(UUID groupId) {
        StudyGroupEntity group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Study group not found with id: " + groupId));

        // Get current student
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        StudentEntity student = studentRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalStateException("Only students can leave study groups"));

        // Check if student is a member
        if (group.getMembers().stream().noneMatch(m -> m.getId().equals(student.getId()))) {
            throw new IllegalStateException("You are not a member of this study group");
        }

        // Remove student from the group
        group.getMembers().removeIf(m -> m.getId().equals(student.getId()));

        // If group becomes empty, deactivate it
        if (group.getMembers().isEmpty()) {
            group.setActive(false);
        }

        StudyGroupEntity updatedGroup = studyGroupRepository.save(group);

        return studyGroupMapper.entityToDto(updatedGroup);
    }

    public List<StudyGroupDto> findStudyGroupsByTopic(String topic) {
        return studyGroupRepository.findByTopic(topic).stream()
                .map(studyGroupMapper::entityToDto)
                .collect(Collectors.toList());
    }

    public List<StudyGroupDto> findStudyGroupsByStudentId(UUID studentId) {
        return studyGroupRepository.findByStudentId(studentId).stream()
                .map(studyGroupMapper::entityToDto)
                .collect(Collectors.toList());
    }

    public List<StudyGroupDto> findAvailableStudyGroups() {
        return studyGroupRepository.findAvailableGroups().stream()
                .map(studyGroupMapper::entityToDto)
                .collect(Collectors.toList());
    }

    public List<StudyGroupDto> recommendStudyGroupsByInterests(UUID studentId) {
        return studyGroupRepository.recommendGroupsByInterests(studentId).stream()
                .map(studyGroupMapper::entityToDto)
                .collect(Collectors.toList());
    }

    public List<StudyGroupDto> findActiveGroupsWithMinMembers(int minMembers) {
        return studyGroupRepository.findActiveGroupsWithMinMembers(minMembers).stream()
                .map(studyGroupMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public StudyGroupDto createStudyGroupAutomatically(Set<String> topics, Integer maxCapacity) {
        // Create a new study group with the given topics
        StudyGroupEntity group = StudyGroupEntity.builder()
                .name("Auto-generated Study Group: " + String.join(", ", topics))
                .description(
                        "Automatically created study group for students interested in: " + String.join(", ", topics))
                .topics(topics)
                .active(true)
                .maxCapacity(maxCapacity)
                .members(new HashSet<>())
                .build();

        // Find students with matching interests
        Set<StudentEntity> matchingStudents = new HashSet<>();
        for (String topic : topics) {
            List<StudentEntity> students = studentRepository.findByAcademicInterest(topic);
            matchingStudents.addAll(students);
        }

        // Add up to maxCapacity students to the group
        if (maxCapacity != null) {
            matchingStudents.stream()
                    .limit(maxCapacity)
                    .forEach(group.getMembers()::add);
        } else {
            group.getMembers().addAll(matchingStudents);
        }

        // Save only if there are members
        if (!group.getMembers().isEmpty()) {
            StudyGroupEntity savedGroup = studyGroupRepository.save(group);
            return studyGroupMapper.entityToDto(savedGroup);
        } else {
            throw new IllegalStateException("No students found with matching interests");
        }
    }
}
