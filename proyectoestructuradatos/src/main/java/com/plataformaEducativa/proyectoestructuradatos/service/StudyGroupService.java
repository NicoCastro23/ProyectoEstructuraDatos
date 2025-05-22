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
        validateGroupStatus(group);

        // Get current student
        StudentEntity student = getCurrentAuthenticatedStudent();

        // Check if student is already a member
        validateStudentNotAlreadyMember(group, student);

        // Add student to the group (bidirectional relationship)
        addStudentToGroup(group, student);

        // Save and return
        StudyGroupEntity updatedGroup = studyGroupRepository.save(group);
        return studyGroupMapper.entityToDto(updatedGroup);
    }

    /**
     * Validates that the group is active and has capacity
     */
    private void validateGroupStatus(StudyGroupEntity group) {
        if (!group.isActive()) {
            throw new IllegalStateException("This study group is no longer active");
        }

        if (group.getMaxCapacity() != null && group.getMembers().size() >= group.getMaxCapacity()) {
            throw new IllegalStateException("This study group has reached its maximum capacity");
        }
    }

    /**
     * Gets the current authenticated student
     */
    private StudentEntity getCurrentAuthenticatedStudent() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return studentRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AccessDeniedException("Only students can join study groups"));
    }

    /**
     * Validates that the student is not already a member
     */
    private void validateStudentNotAlreadyMember(StudyGroupEntity group, StudentEntity student) {
        if (group.getMembers().stream().anyMatch(m -> m.getId().equals(student.getId()))) {
            throw new IllegalStateException("You are already a member of this study group");
        }
    }

    /**
     * Adds student to group maintaining bidirectional relationship
     */
    private void addStudentToGroup(StudyGroupEntity group, StudentEntity student) {
        group.getMembers().add(student);
        student.getStudyGroups().add(group); // ← Esta línea es crucial
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

    /**
     * Crea automáticamente un grupo de estudio y agrega estudiantes con intereses
     * coincidentes
     * 
     * @param topics      Conjunto de temas del grupo
     * @param maxCapacity Capacidad máxima del grupo (opcional)
     * @return DTO del grupo creado con miembros agregados
     * @throws IllegalArgumentException si los temas están vacíos
     * @throws IllegalStateException    si no se encuentran estudiantes con
     *                                  intereses coincidentes
     */
    @Transactional
    public StudyGroupDto createStudyGroupAutomatically(Set<String> topics, Integer maxCapacity) {
        validateTopics(topics);

        StudyGroupEntity group = createBaseGroup(topics, maxCapacity);
        Set<StudentEntity> matchingStudents = findStudentsWithMatchingInterests(topics);

        validateMatchingStudents(matchingStudents);

        Set<StudentEntity> selectedStudents = selectStudentsForGroup(matchingStudents, maxCapacity);
        addStudentsToGroupBidirectionally(group, selectedStudents);

        StudyGroupEntity savedGroup = studyGroupRepository.save(group);

        return studyGroupMapper.entityToDto(savedGroup);
    }

    /**
     * Valida que los temas no estén vacíos
     * 
     * @param topics Conjunto de temas a validar
     * @throws IllegalArgumentException si está vacío o nulo
     */
    private void validateTopics(Set<String> topics) {
        if (topics == null || topics.isEmpty()) {
            throw new IllegalArgumentException("At least one topic must be provided");
        }
    }

    /**
     * Crea la entidad base del grupo de estudio
     * 
     * @param topics      Temas del grupo
     * @param maxCapacity Capacidad máxima
     * @return StudyGroupEntity base creado
     */
    private StudyGroupEntity createBaseGroup(Set<String> topics, Integer maxCapacity) {
        String groupName = generateGroupName(topics);
        String groupDescription = generateGroupDescription(topics);

        return StudyGroupEntity.builder()
                .name(groupName)
                .description(groupDescription)
                .topics(new HashSet<>(topics)) // Crear nueva instancia para evitar referencias
                .active(true)
                .maxCapacity(maxCapacity)
                .members(new HashSet<>())
                .build();
    }

    /**
     * Genera el nombre del grupo basado en los temas
     * 
     * @param topics Temas del grupo
     * @return Nombre del grupo generado
     */
    private String generateGroupName(Set<String> topics) {
        return "Auto-generated Study Group: " + String.join(", ", topics);
    }

    /**
     * Genera la descripción del grupo basada en los temas
     * 
     * @param topics Temas del grupo
     * @return Descripción del grupo generada
     */
    private String generateGroupDescription(Set<String> topics) {
        return "Automatically created study group for students interested in: " +
                String.join(", ", topics);
    }

    /**
     * Busca estudiantes con intereses que coincidan con los temas del grupo
     * 
     * @param topics Temas a buscar
     * @return Conjunto de estudiantes con intereses coincidentes
     */
    private Set<StudentEntity> findStudentsWithMatchingInterests(Set<String> topics) {
        Set<StudentEntity> matchingStudents = new HashSet<>();

        for (String topic : topics) {
            List<StudentEntity> studentsWithInterest = studentRepository.findByAcademicInterest(topic);
            matchingStudents.addAll(studentsWithInterest);
        }

        return matchingStudents;
    }

    /**
     * Valida que se hayan encontrado estudiantes con intereses coincidentes
     * 
     * @param matchingStudents Estudiantes encontrados
     * @throws IllegalStateException si no se encontraron estudiantes
     */
    private void validateMatchingStudents(Set<StudentEntity> matchingStudents) {
        if (matchingStudents.isEmpty()) {
            throw new IllegalStateException("No students found with matching interests for the specified topics");
        }
    }

    /**
     * Selecciona los estudiantes que se agregarán al grupo respetando la capacidad
     * máxima
     * 
     * @param matchingStudents Estudiantes candidatos
     * @param maxCapacity      Capacidad máxima del grupo
     * @return Conjunto de estudiantes seleccionados
     */
    private Set<StudentEntity> selectStudentsForGroup(Set<StudentEntity> matchingStudents, Integer maxCapacity) {
        if (maxCapacity == null) {
            return new HashSet<>(matchingStudents);
        }

        return matchingStudents.stream()
                .limit(maxCapacity)
                .collect(Collectors.toSet());
    }

    /**
     * Agrega estudiantes al grupo estableciendo la relación bidireccional
     * correctamente
     * 
     * @param group    Grupo de estudio
     * @param students Estudiantes a agregar
     */
    private void addStudentsToGroupBidirectionally(StudyGroupEntity group, Set<StudentEntity> students) {
        for (StudentEntity student : students) {
            // Establecer relación bidireccional
            group.getMembers().add(student); // Lado del grupo
            student.getStudyGroups().add(group); // Lado del estudiante
        }
    }
}
