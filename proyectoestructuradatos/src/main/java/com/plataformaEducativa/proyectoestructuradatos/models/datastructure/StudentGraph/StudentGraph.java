package com.plataformaEducativa.proyectoestructuradatos.models.datastructure.StudentGraph;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

import com.plataformaEducativa.proyectoestructuradatos.models.Student;

/**
 * Implementación de un grafo no dirigido para representar las conexiones entre
 * estudiantes.
 * Los nodos son estudiantes y las aristas indican intereses académicos en
 * común.
 */
public class StudentGraph {

    // Mapa que asocia cada estudiante con sus conexiones (adyacencias)
    @Getter
    private final Map<Student, Map<Student, Integer>> adjacencyMap;

    public StudentGraph() {
        this.adjacencyMap = new HashMap<>();
    }

    /**
     * Añade un estudiante al grafo.
     * 
     * @param student Estudiante a añadir
     */
    public void addStudent(Student student) {
        if (!adjacencyMap.containsKey(student)) {
            adjacencyMap.put(student, new HashMap<>());
        }
    }

    /**
     * Añade una conexión entre dos estudiantes con un peso específico.
     * 
     * @param studentA Primer estudiante
     * @param studentB Segundo estudiante
     * @param weight   Peso de la conexión (fuerza de la relación, basada en
     *                 intereses comunes)
     */
    public void addConnection(Student studentA, Student studentB, int weight) {
        // Asegurarse de que ambos estudiantes existen en el grafo
        addStudent(studentA);
        addStudent(studentB);

        // Añadir conexión en ambas direcciones (grafo no dirigido)
        adjacencyMap.get(studentA).put(studentB, weight);
        adjacencyMap.get(studentB).put(studentA, weight);
    }

    /**
     * Elimina una conexión entre dos estudiantes.
     * 
     * @param studentA Primer estudiante
     * @param studentB Segundo estudiante
     */
    public void removeConnection(Student studentA, Student studentB) {
        if (adjacencyMap.containsKey(studentA)) {
            adjacencyMap.get(studentA).remove(studentB);
        }

        if (adjacencyMap.containsKey(studentB)) {
            adjacencyMap.get(studentB).remove(studentA);
        }
    }

    /**
     * Elimina un estudiante y todas sus conexiones del grafo.
     * 
     * @param student Estudiante a eliminar
     */
    public void removeStudent(Student student) {
        // Eliminar todas las conexiones con este estudiante
        for (Map<Student, Integer> connections : adjacencyMap.values()) {
            connections.remove(student);
        }

        // Eliminar el estudiante del grafo
        adjacencyMap.remove(student);
    }

    /**
     * Obtiene todos los estudiantes conectados directamente con un estudiante
     * específico.
     * 
     * @param student Estudiante del que se quieren obtener las conexiones
     * @return Lista de estudiantes conectados
     */
    public List<Student> getConnections(Student student) {
        if (!adjacencyMap.containsKey(student)) {
            return new ArrayList<>();
        }

        return new ArrayList<>(adjacencyMap.get(student).keySet());
    }

    /**
     * Obtiene el peso de la conexión entre dos estudiantes.
     * 
     * @param studentA Primer estudiante
     * @param studentB Segundo estudiante
     * @return Peso de la conexión, o 0 si no hay conexión
     */
    public int getConnectionWeight(Student studentA, Student studentB) {
        if (!adjacencyMap.containsKey(studentA) || !adjacencyMap.get(studentA).containsKey(studentB)) {
            return 0;
        }

        return adjacencyMap.get(studentA).get(studentB);
    }

    /**
     * Incrementa el peso de una conexión existente.
     * 
     * @param studentA  Primer estudiante
     * @param studentB  Segundo estudiante
     * @param increment Cantidad a incrementar
     */
    public void incrementConnectionWeight(Student studentA, Student studentB, int increment) {
        if (!adjacencyMap.containsKey(studentA) || !adjacencyMap.get(studentA).containsKey(studentB)) {
            return;
        }

        int currentWeight = adjacencyMap.get(studentA).get(studentB);
        adjacencyMap.get(studentA).put(studentB, currentWeight + increment);
        adjacencyMap.get(studentB).put(studentA, currentWeight + increment);
    }

    /**
     * Encuentra el camino más corto entre dos estudiantes usando el algoritmo de
     * Dijkstra.
     * 
     * @param start Estudiante de inicio
     * @param end   Estudiante final
     * @return Lista de estudiantes que forman el camino más corto
     */
    public List<Student> findShortestPath(Student start, Student end) {
        if (!adjacencyMap.containsKey(start) || !adjacencyMap.containsKey(end)) {
            return List.of();
        }

        Map<Student, Integer> distances = new HashMap<>();
        Map<Student, Student> previousNodes = new HashMap<>();
        PriorityQueue<StudentGraphNode> queue = new PriorityQueue<>();
        Set<Student> visited = new HashSet<>();

        // Inicializar distancias
        for (Student student : adjacencyMap.keySet()) {
            distances.put(student, Integer.MAX_VALUE);
        }
        distances.put(start, 0);
        queue.add(new StudentGraphNode(start, 0));

        while (!queue.isEmpty()) {
            StudentGraphNode current = queue.poll();
            Student currentStudent = current.getStudent();

            if (currentStudent.equals(end)) {
                break;
            }

            if (visited.contains(currentStudent)) {
                continue;
            }

            visited.add(currentStudent);

            // Revisar todos los vecinos
            for (Map.Entry<Student, Integer> neighbor : adjacencyMap.get(currentStudent).entrySet()) {
                Student neighborStudent = neighbor.getKey();
                int weight = neighbor.getValue();

                if (visited.contains(neighborStudent)) {
                    continue;
                }

                int distance = distances.get(currentStudent) + weight;

                if (distance < distances.get(neighborStudent)) {
                    distances.put(neighborStudent, distance);
                    previousNodes.put(neighborStudent, currentStudent);
                    queue.add(new StudentGraphNode(neighborStudent, distance));
                }
            }
        }

        // Reconstruir el camino
        List<Student> path = new ArrayList<>();
        Student current = end;

        if (!previousNodes.containsKey(end)) {
            return path; // No hay camino
        }

        while (current != null) {
            path.add(0, current);
            current = previousNodes.get(current);
        }

        return path;
    }

    /**
     * Encuentra estudiantes que podrían ser recomendados a un estudiante específico
     * (amigos de amigos que aún no son amigos directos).
     * 
     * @param student Estudiante para el que se buscan recomendaciones
     * @return Mapa de estudiantes recomendados con su puntuación de recomendación
     */
    public Map<Student, Integer> getRecommendations(Student student) {
        if (!adjacencyMap.containsKey(student)) {
            return new HashMap<>();
        }

        Map<Student, Integer> recommendations = new HashMap<>();
        Set<Student> directConnections = adjacencyMap.get(student).keySet();

        // Si no tiene conexiones directas, recomendar estudiantes con más conexiones
        if (directConnections.isEmpty()) {
            // Recomendar los estudiantes más conectados
            for (Map.Entry<Student, Map<Student, Integer>> entry : adjacencyMap.entrySet()) {
                Student otherStudent = entry.getKey();

                if (!otherStudent.equals(student)) {
                    int connectionCount = entry.getValue().size();
                    if (connectionCount > 0) {
                        // Usar el número de conexiones como puntuación
                        recommendations.put(otherStudent, connectionCount * 10);
                    }
                }
            }

            // Limitar a los top 10
            return recommendations.entrySet().stream()
                    .sorted(Map.Entry.<Student, Integer>comparingByValue().reversed())
                    .limit(10)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new));
        }

        // Lógica original para estudiantes con conexiones
        // Para cada conexión directa
        for (Student connection : directConnections) {
            // Considerar todas las conexiones de esta conexión directa
            for (Map.Entry<Student, Integer> secondDegree : adjacencyMap.get(connection).entrySet()) {
                Student potentialRecommendation = secondDegree.getKey();

                // Ignorar si es el estudiante original o ya es una conexión directa
                if (potentialRecommendation.equals(student) || directConnections.contains(potentialRecommendation)) {
                    continue;
                }

                // Calcular puntuación para esta recomendación
                int recommendationScore = secondDegree.getValue() * adjacencyMap.get(student).get(connection);

                // Actualizar puntuación
                recommendations.put(
                        potentialRecommendation,
                        recommendations.getOrDefault(potentialRecommendation, 0) + recommendationScore);
            }
        }

        return recommendations;
    }

    /**
     * Obtiene los estudiantes más conectados eliminando duplicados
     * 
     * @param limit Número máximo de estudiantes a retornar
     * @return Lista de estudiantes únicos con connectionCount correcto
     */
    public List<Student> getMostConnectedStudents(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }

        // Usar Map para eliminar duplicados por ID
        Map<UUID, Student> uniqueStudents = new HashMap<>();

        for (Map.Entry<Student, Map<Student, Integer>> entry : adjacencyMap.entrySet()) {
            Student student = entry.getKey();
            int connectionCount = entry.getValue().size();

            // Solo mantener el estudiante con mayor connectionCount
            UUID studentId = student.getId();
            if (!uniqueStudents.containsKey(studentId) ||
                    uniqueStudents.get(studentId).getConnectionCount() < connectionCount) {

                Student studentWithConnections = createStudentWithConnectionCount(student, connectionCount);
                uniqueStudents.put(studentId, studentWithConnections);
            }
        }

        return uniqueStudents.values()
                .stream()
                .sorted((a, b) -> Integer.compare(b.getConnectionCount(), a.getConnectionCount()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Crea una nueva instancia de Student con el connectionCount establecido
     * 
     * @param originalStudent Estudiante original
     * @param connectionCount Número de conexiones
     * @return Student con connectionCount correcto
     */
    private Student createStudentWithConnectionCount(Student originalStudent, int connectionCount) {
        return Student.builder()
                .id(originalStudent.getId())
                .username(originalStudent.getUsername())
                .fullName(originalStudent.getFullName())
                .email(originalStudent.getEmail())
                .connectionCount(connectionCount)
                .build();
    }

    /**
     * Alternativa más simple usando Set para eliminar duplicados
     */
    public List<Student> getMostConnectedStudentsAlternative(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }

        // Usar Set con comparación por ID para eliminar duplicados
        Set<Student> uniqueStudents = new LinkedHashSet<>();

        // Procesar adjacencyMap y mantener solo estudiantes únicos con connectionCount
        // correcto
        adjacencyMap.entrySet()
                .stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.getKey().getId(), // Agrupar por ID
                        Collectors.maxBy(Comparator.comparing(entry -> entry.getValue().size())) // Mantener el de mayor
                                                                                                 // connectionCount
                ))
                .values()
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(entry -> {
                    Student student = entry.getKey();
                    int connectionCount = entry.getValue().size();
                    Student studentWithConnections = createStudentWithConnectionCount(student, connectionCount);
                    uniqueStudents.add(studentWithConnections);
                });

        return uniqueStudents.stream()
                .sorted((a, b) -> Integer.compare(b.getConnectionCount(), a.getConnectionCount()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Detecta comunidades de estudio (subgrafos densamente conectados).
     * Implementación simple basada en cliques
     * 
     * @return Lista de comunidades (cada comunidad es un conjunto de estudiantes)
     */
    public List<Set<Student>> detectCommunities() {
        List<Set<Student>> communities = new ArrayList<>();
        Set<Student> unassigned = new HashSet<>(adjacencyMap.keySet());

        while (!unassigned.isEmpty()) {
            // Tomar un estudiante sin asignar
            Student seed = unassigned.iterator().next();
            Set<Student> community = new HashSet<>();
            community.add(seed);

            // Encontrar todos los estudiantes fuertemente conectados a este
            for (Student student : adjacencyMap.keySet()) {
                if (!student.equals(seed) && adjacencyMap.get(seed).containsKey(student) &&
                        adjacencyMap.get(seed).get(student) > 1) {
                    community.add(student);
                }
            }

            // Refinar la comunidad eliminando conexiones débiles
            boolean changed;
            do {
                changed = false;
                for (Student student : new HashSet<>(community)) {
                    int strongConnections = 0;
                    for (Student other : community) {
                        if (!student.equals(other) && adjacencyMap.get(student).containsKey(other) &&
                                adjacencyMap.get(student).get(other) > 1) {
                            strongConnections++;
                        }
                    }

                    // Si menos de la mitad de las conexiones son fuertes, eliminar de la comunidad
                    if (strongConnections < community.size() / 2) {
                        community.remove(student);
                        changed = true;
                    }
                }
            } while (changed);

            // Si la comunidad tiene más de un estudiante, añadirla a la lista
            if (community.size() > 1) {
                communities.add(community);
                unassigned.removeAll(community);
            } else {
                // Si solo queda un estudiante, eliminar de los no asignados
                unassigned.remove(seed);
            }
        }

        return communities;
    }
}