package com.plataformaEducativa.proyectoestructuradatos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plataformaEducativa.proyectoestructuradatos.entity.StudentEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.StudentConnectionEntity;
import com.plataformaEducativa.proyectoestructuradatos.exception.ResourceNotFoundException;
import com.plataformaEducativa.proyectoestructuradatos.models.Student;
import com.plataformaEducativa.proyectoestructuradatos.models.datastructure.StudentGraph.StudentGraph;
import com.plataformaEducativa.proyectoestructuradatos.repository.StudentConnectionRepository;
import com.plataformaEducativa.proyectoestructuradatos.repository.StudentRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentConnectionService {

    private final StudentConnectionRepository connectionRepository;
    private final StudentRepository studentRepository;
    private final StudentService studentService;

    @Transactional
    public StudentConnectionEntity createConnection(UUID studentIdA, UUID studentIdB, Set<String> commonInterests) {
        // Ensure students exist
        StudentEntity studentA = studentRepository.findById(studentIdA)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentIdA));

        StudentEntity studentB = studentRepository.findById(studentIdB)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentIdB));

        // Check if connection already exists
        Optional<StudentConnectionEntity> existingConnection = connectionRepository.findConnection(studentIdA,
                studentIdB);

        if (existingConnection.isPresent()) {
            // Update existing connection
            StudentConnectionEntity connection = existingConnection.get();
            connection.getCommonInterests().addAll(commonInterests);
            connection.setConnectionStrength(connection.getConnectionStrength() + 1);
            connection.setLastInteraction(LocalDateTime.now());
            return connectionRepository.save(connection);
        } else {
            // Create new connection
            StudentConnectionEntity connection = StudentConnectionEntity.builder()
                    .studentA(studentA)
                    .studentB(studentB)
                    .connectionStrength(1)
                    .commonInterests(commonInterests)
                    .lastInteraction(LocalDateTime.now())
                    .build();
            return connectionRepository.save(connection);
        }
    }

    @Transactional
    public StudentConnectionEntity updateConnection(UUID studentIdA, UUID studentIdB, Integer strengthChange) {
        // Find connection
        StudentConnectionEntity connection = connectionRepository.findConnection(studentIdA, studentIdB)
                .orElseThrow(() -> new ResourceNotFoundException("Connection not found between students"));

        // Update strength
        if (strengthChange != null) {
            int newStrength = connection.getConnectionStrength() + strengthChange;
            connection.setConnectionStrength(Math.max(1, newStrength)); // Minimum strength is 1
        }

        connection.setLastInteraction(LocalDateTime.now());
        return connectionRepository.save(connection);
    }

    @Transactional
    public void deleteConnection(UUID studentIdA, UUID studentIdB) {
        StudentConnectionEntity connection = connectionRepository.findConnection(studentIdA, studentIdB)
                .orElseThrow(() -> new ResourceNotFoundException("Connection not found between students"));

        connectionRepository.delete(connection);
    }

    public List<StudentConnectionEntity> getStudentConnections(UUID studentId) {
        return connectionRepository.findConnectionsByStudentId(studentId);
    }

    public Set<UUID> getConnectedStudentIds(UUID studentId) {
        List<StudentConnectionEntity> connections = connectionRepository.findConnectionsByStudentId(studentId);

        return connections.stream()
                .map(conn -> conn.getStudentA().getId().equals(studentId) ? conn.getStudentB().getId()
                        : conn.getStudentA().getId())
                .collect(Collectors.toSet());
    }

    public List<Map<String, Object>> findShortestPath(UUID startStudentId, UUID endStudentId) {
        // Use graph algorithm to find path
        StudentGraph graph = studentService.buildStudentGraph();
        List<Student> path = graph.findShortestPath(
                graph.getAdjacencyMap().keySet().stream()
                        .filter(s -> s.getId().equals(startStudentId))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Start student not found in graph")),
                graph.getAdjacencyMap().keySet().stream()
                        .filter(s -> s.getId().equals(endStudentId))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("End student not found in graph")));

        // Convert path to DTO format
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < path.size(); i++) {
            Map<String, Object> node = new HashMap<>();
            node.put("position", i);
            node.put("studentId", path.get(i).getId());
            node.put("username", path.get(i).getUsername());
            node.put("fullName", path.get(i).getFullName());

            if (i < path.size() - 1) {
                // Get connection details between current and next student
                Optional<StudentConnectionEntity> connection = connectionRepository.findConnection(
                        path.get(i).getId(), path.get(i + 1).getId());

                if (connection.isPresent()) {
                    node.put("connectionStrength", connection.get().getConnectionStrength());
                    node.put("commonInterests", connection.get().getCommonInterests());
                }
            }

            result.add(node);
        }

        return result;
    }

    public Map<UUID, Integer> getStudentRecommendations(UUID studentId) {
        StudentGraph graph = studentService.buildStudentGraph();
        Student student = graph.getAdjacencyMap().keySet().stream()
                .filter(s -> s.getId().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Student not found in graph"));

        Map<Student, Integer> recommendations = graph.getRecommendations(student);

        // Convert to UUID -> Score map
        return recommendations.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getId(),
                        Map.Entry::getValue));
    }

    public List<Student> getMostConnectedStudents(int limit) {
        StudentGraph graph = studentService.buildStudentGraph();
        return graph.getMostConnectedStudents(limit);
    }

    public List<Set<Student>> detectCommunities() {
        StudentGraph graph = studentService.buildStudentGraph();
        return graph.detectCommunities();
    }

    @Transactional
    public void generateStudyPartnerConnections() {
        // Get all students with their study groups
        List<StudentEntity> allStudents = studentRepository.findAll();

        // For each student, create connections with other students in the same study
        // groups
        for (StudentEntity studentA : allStudents) {
            Set<StudentEntity> groupMembers = studentA.getStudyGroups().stream()
                    .flatMap(group -> group.getMembers().stream())
                    .filter(studentB -> !studentB.getId().equals(studentA.getId()))
                    .collect(Collectors.toSet());

            for (StudentEntity studentB : groupMembers) {
                // Find common interests
                Set<String> commonInterests = new HashSet<>(studentA.getAcademicInterests());
                commonInterests.retainAll(studentB.getAcademicInterests());

                // Create or update connection
                Optional<StudentConnectionEntity> existingConnection = connectionRepository
                        .findConnection(studentA.getId(), studentB.getId());

                if (existingConnection.isPresent()) {
                    StudentConnectionEntity connection = existingConnection.get();
                    connection.getCommonInterests().addAll(commonInterests);
                    connection.setConnectionStrength(connection.getConnectionStrength() + 1);
                    connection.setLastInteraction(LocalDateTime.now());
                    connectionRepository.save(connection);
                } else {
                    StudentConnectionEntity connection = StudentConnectionEntity.builder()
                            .studentA(studentA)
                            .studentB(studentB)
                            .connectionStrength(1)
                            .commonInterests(commonInterests)
                            .lastInteraction(LocalDateTime.now())
                            .build();
                    connectionRepository.save(connection);
                }
            }
        }
    }

    @Transactional
    public void generateSimilarContentConnections() {
        // Get all students who have rated similar content highly
        List<StudentEntity> allStudents = studentRepository.findAll();

        // Compare each pair of students for similar ratings
        for (int i = 0; i < allStudents.size(); i++) {
            StudentEntity studentA = allStudents.get(i);

            for (int j = i + 1; j < allStudents.size(); j++) {
                StudentEntity studentB = allStudents.get(j);

                // Find common content ratings
                Set<UUID> studentARatedContentIds = studentA.getPublishedContents().stream()
                        .map(c -> c.getId())
                        .collect(Collectors.toSet());

                Set<UUID> studentBRatedContentIds = studentB.getPublishedContents().stream()
                        .map(c -> c.getId())
                        .collect(Collectors.toSet());

                // Find intersection
                Set<UUID> commonContentIds = new HashSet<>(studentARatedContentIds);
                commonContentIds.retainAll(studentBRatedContentIds);

                // If students have rated at least 2 common contents, create or strengthen
                // connection
                if (commonContentIds.size() >= 2) {
                    Set<String> commonInterests = new HashSet<>(studentA.getAcademicInterests());
                    commonInterests.retainAll(studentB.getAcademicInterests());

                    // Create or update connection
                    Optional<StudentConnectionEntity> existingConnection = connectionRepository
                            .findConnection(studentA.getId(), studentB.getId());

                    if (existingConnection.isPresent()) {
                        StudentConnectionEntity connection = existingConnection.get();
                        connection.getCommonInterests().addAll(commonInterests);
                        connection.setConnectionStrength(connection.getConnectionStrength() + commonContentIds.size());
                        connection.setLastInteraction(LocalDateTime.now());
                        connectionRepository.save(connection);
                    } else {
                        StudentConnectionEntity connection = StudentConnectionEntity.builder()
                                .studentA(studentA)
                                .studentB(studentB)
                                .connectionStrength(commonContentIds.size())
                                .commonInterests(commonInterests)
                                .lastInteraction(LocalDateTime.now())
                                .build();
                        connectionRepository.save(connection);
                    }
                }
            }
        }
    }
}