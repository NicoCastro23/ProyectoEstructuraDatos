package com.plataformaEducativa.proyectoestructuradatos.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plataformaEducativa.proyectoestructuradatos.dto.StudentDto;
import com.plataformaEducativa.proyectoestructuradatos.entity.StudentEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.StudentConnectionEntity;
import com.plataformaEducativa.proyectoestructuradatos.exception.ResourceNotFoundException;
import com.plataformaEducativa.proyectoestructuradatos.mapper.StudentMapper;
import com.plataformaEducativa.proyectoestructuradatos.models.Student;
import com.plataformaEducativa.proyectoestructuradatos.models.datastructure.StudentGraph.StudentGraph;
import com.plataformaEducativa.proyectoestructuradatos.repository.StudentConnectionRepository;
import com.plataformaEducativa.proyectoestructuradatos.repository.StudentRepository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentConnectionRepository connectionRepository;
    private final StudentMapper studentMapper;
    private final PasswordEncoder passwordEncoder;

    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(entity -> {
                    StudentDto dto = studentMapper.entityToDto(entity);

                    // Calcular connectionCount usando el repositorio
                    int connectionCount = calculateConnectionCountForStudent(entity.getId());
                    dto.setConnectionCount(connectionCount);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Calcula el número de conexiones únicas para un estudiante
     * 
     * @param studentId ID del estudiante
     * @return Número de estudiantes diferentes conectados
     */
    private int calculateConnectionCountForStudent(UUID studentId) {
        try {
            // Usar el método que ya tienes en el repositorio
            List<StudentConnectionEntity> connections = connectionRepository.findConnectionsByStudentId(studentId);

            // Contar estudiantes únicos conectados
            Set<UUID> uniqueConnectedStudents = new HashSet<>();

            for (StudentConnectionEntity connection : connections) {
                // Agregar el otro estudiante (no el mismo)
                if (connection.getStudentA().getId().equals(studentId)) {
                    uniqueConnectedStudents.add(connection.getStudentB().getId());
                } else {
                    uniqueConnectedStudents.add(connection.getStudentA().getId());
                }
            }

            return uniqueConnectedStudents.size();

        } catch (Exception e) {
            // Log del error y retornar 0 si algo falla

            return 0;
        }
    }

    // Resto de tus métodos existentes sin cambios...

    public StudentDto getStudentById(UUID id) {
        StudentEntity student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        StudentDto dto = studentMapper.entityToDto(student);

        // Add connection count
        long connectionCount = connectionRepository.findConnectionsByStudentId(id).size();
        dto.setConnectionCount((int) connectionCount);

        return dto;
    }

    public StudentDto getStudentByUsername(String username) {
        StudentEntity student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with username: " + username));

        StudentDto dto = studentMapper.entityToDto(student);

        // Add connection count
        long connectionCount = connectionRepository.findConnectionsByStudentId(student.getId()).size();
        dto.setConnectionCount((int) connectionCount);

        return dto;
    }

    @Transactional
    public StudentDto updateStudent(UUID id, StudentDto studentDto) {
        StudentEntity student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        // Security check - only allow users to update their own profile unless they are
        // moderators
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isModerator = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR"));

        if (!student.getUsername().equals(currentUsername) && !isModerator) {
            throw new AccessDeniedException("You are not authorized to update this student profile");
        }

        studentMapper.updateEntityFromDto(studentDto, student);
        StudentEntity updatedStudent = studentRepository.save(student);

        return studentMapper.entityToDto(updatedStudent);
    }

    @Transactional
    public StudentDto updatePassword(UUID id, String currentPassword, String newPassword) {
        StudentEntity student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        // Security check - only allow users to update their own password
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!student.getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("You are not authorized to change this student's password");
        }

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, student.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Update password
        student.setPassword(passwordEncoder.encode(newPassword));
        StudentEntity updatedStudent = studentRepository.save(student);

        return studentMapper.entityToDto(updatedStudent);
    }

    public StudentGraph buildStudentGraph() {
        List<StudentEntity> allStudents = studentRepository.findAll();
        List<StudentConnectionEntity> allConnections = connectionRepository.findAll();

        log.info("Building graph with {} students and {} connections",
                allStudents.size(), allConnections.size());

        StudentGraph graph = new StudentGraph();

        // Mapa para mantener referencia a los objetos Student por ID
        Map<UUID, Student> studentMap = new HashMap<>();

        // Add all students to the graph
        for (StudentEntity entity : allStudents) {
            Student student = studentMapper.entityToModel(entity);
            // Add connection count
            long connectionCount = connectionRepository.findConnectionsByStudentId(entity.getId()).size();
            student.setConnectionCount((int) connectionCount);

            graph.addStudent(student);
            studentMap.put(student.getId(), student); // Guardar referencia
        }

        // Add all connections to the graph
        for (StudentConnectionEntity connection : allConnections) {
            // Usar las referencias existentes en lugar de crear nuevas
            Student studentA = studentMap.get(connection.getStudentA().getId());
            Student studentB = studentMap.get(connection.getStudentB().getId());

            if (studentA != null && studentB != null) {
                log.debug("Adding connection between {} and {} with strength {}",
                        studentA.getFullName(), studentB.getFullName(),
                        connection.getConnectionStrength());

                graph.addConnection(studentA, studentB, connection.getConnectionStrength());
            } else {
                log.warn("Could not find students for connection: {} - {}",
                        connection.getStudentA().getId(),
                        connection.getStudentB().getId());
            }
        }

        // Debug: imprimir estadísticas del grafo
        log.info("Graph built successfully");
        int totalConnections = 0;
        for (Map.Entry<Student, Map<Student, Integer>> entry : graph.getAdjacencyMap().entrySet()) {
            totalConnections += entry.getValue().size();
        }
        log.info("Total connections in graph: {}", totalConnections / 2); // Dividido por 2 porque es no dirigido

        return graph;
    }

    public List<StudentDto> findStudentsByInterest(String interest) {
        return studentRepository.findByAcademicInterest(interest).stream()
                .map(entity -> {
                    StudentDto dto = studentMapper.entityToDto(entity);

                    // Usar el mismo método que creamos para getAllStudents()
                    dto.setConnectionCount(calculateConnectionCountForStudent(entity.getId()));

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<StudentDto> findStudentsByFieldOfStudy(String field) {
        return studentRepository.findByFieldOfStudy(field).stream()
                .map(entity -> {
                    StudentDto dto = studentMapper.entityToDto(entity);

                    // Calcular connectionCount usando el método existente
                    dto.setConnectionCount(calculateConnectionCountForStudent(entity.getId()));

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<StudentDto> findStudentsByStudyGroup(UUID groupId) {
        return studentRepository.findByStudyGroupId(groupId).stream()
                .map(entity -> {
                    StudentDto dto = studentMapper.entityToDto(entity);

                    // Calcular connectionCount usando el método existente
                    dto.setConnectionCount(calculateConnectionCountForStudent(entity.getId()));

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<StudentDto> findStudentsWithCommonGroups(UUID studentId) {
        return studentRepository.findStudentsWithCommonGroups(studentId).stream()
                .map(entity -> {
                    StudentDto dto = studentMapper.entityToDto(entity);

                    // Calcular connectionCount usando el método existente
                    dto.setConnectionCount(calculateConnectionCountForStudent(entity.getId()));

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Set<String> getAllAcademicInterests() {
        return studentRepository.findAll().stream()
                .flatMap(Estudiante -> Estudiante.getAcademicInterests().stream())
                .collect(Collectors.toSet());
    }

    public Set<String> getAllFieldsOfStudy() {
        return studentRepository.findAll().stream()
                .map(StudentEntity::getFieldOfStudy)
                .collect(Collectors.toSet());
    }
}
