package com.plataformaEducativa.proyectoestructuradatos.models.datastructure.StudentGraph;

import com.plataformaEducativa.proyectoestructuradatos.models.Student;

import lombok.Getter;
import lombok.Setter;

/**
 * Nodo utilizado para el algoritmo de Dijkstra en el grafo de estudiantes.
 * Representa un estudiante con su distancia en el algoritmo.
 */
@Getter
@Setter
public class StudentGraphNode implements Comparable<StudentGraphNode> {
    private Student student;
    private int distance;

    public StudentGraphNode(Student student, int distance) {
        this.student = student;
        this.distance = distance;
    }

    @Override
    public int compareTo(StudentGraphNode other) {
        return Integer.compare(this.distance, other.distance);
    }
}