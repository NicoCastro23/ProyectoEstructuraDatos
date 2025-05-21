package com.plataformaEducativa.proyectoestructuradatos.service;

import lombok.RequiredArgsConstructor;
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

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentConnectionRepository connectionRepository;
    private final StudentMapper studentMapper;
    private final PasswordEncoder passwordEncoder;

    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(studentMapper::entityToDto)
                .collect(Collectors.toList());
    }

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

        StudentGraph graph = new StudentGraph();

        // Add all students to the graph
        for (StudentEntity entity : allStudents) {
            Student student = studentMapper.entityToModel(entity);
            // Add connection count
            long connectionCount = connectionRepository.findConnectionsByStudentId(entity.getId()).size();
            student.setConnectionCount((int) connectionCount);
            graph.addStudent(student);
        }

        // Add all connections to the graph
        for (StudentConnectionEntity connection : allConnections) {
            Student studentA = studentMapper.entityToModel(connection.getStudentA());
            Student studentB = studentMapper.entityToModel(connection.getStudentB());
            graph.addConnection(studentA, studentB, connection.getConnectionStrength());
        }

        return graph;
    }

    public List<StudentDto> findStudentsByInterest(String interest) {
        return studentRepository.findByAcademicInterest(interest).stream()
                .map(studentMapper::entityToDto)
                .collect(Collectors.toList());
    }

    public List<StudentDto> findStudentsByFieldOfStudy(String field) {
        return studentRepository.findByFieldOfStudy(field).stream()
                .map(studentMapper::entityToDto)
                .collect(Collectors.toList());
    }

    public List<StudentDto> findStudentsByStudyGroup(UUID groupId) {
        return studentRepository.findByStudyGroupId(groupId).stream()
                .map(studentMapper::entityToDto)
                .collect(Collectors.toList());
    }

    public List<StudentDto> findStudentsWithCommonGroups(UUID studentId) {
        return studentRepository.findStudentsWithCommonGroups(studentId).stream()
                .map(studentMapper::entityToDto)
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
