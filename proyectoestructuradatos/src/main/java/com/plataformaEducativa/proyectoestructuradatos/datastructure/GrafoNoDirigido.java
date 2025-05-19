package com.plataformaEducativa.proyectoestructuradatos.datastructure;

import java.util.*;

/**
 * Implementación de un grafo no dirigido para representar la red de afinidad entre estudiantes.
 * @param <T> Tipo de datos de los nodos del grafo
 */
public class GrafoNoDirigido<T> {
    private Map<T, Set<T>> adjacencyList;
    
    public GrafoNoDirigido() {
        this.adjacencyList = new HashMap<>();
    }
    
    /**
     * Añade un nodo al grafo
     * @param node Nodo a añadir
     */
    public void addNode(T node) {
        if (!adjacencyList.containsKey(node)) {
            adjacencyList.put(node, new HashSet<>());
        }
    }
    
    /**
     * Añade una conexión entre dos nodos
     * @param node1 Primer nodo
     * @param node2 Segundo nodo
     */
    public void addEdge(T node1, T node2) {
        addNode(node1);
        addNode(node2);
        
        adjacencyList.get(node1).add(node2);
        adjacencyList.get(node2).add(node1); // Es no dirigido, así que se añade en ambas direcciones
    }
    
    /**
     * Verifica si existe una conexión entre dos nodos
     * @param node1 Primer nodo
     * @param node2 Segundo nodo
     * @return true si existe conexión, false en caso contrario
     */
    public boolean hasEdge(T node1, T node2) {
        return adjacencyList.containsKey(node1) && 
               adjacencyList.containsKey(node2) && 
               adjacencyList.get(node1).contains(node2);
    }
    
    /**
     * Obtiene todos los nodos conectados a un nodo específico
     * @param node Nodo a consultar
     * @return Set con los nodos conectados
     */
    public Set<T> getNeighbors(T node) {
        return adjacencyList.getOrDefault(node, new HashSet<>());
    }
    
    /**
     * Elimina un nodo y todas sus conexiones del grafo
     * @param node Nodo a eliminar
     */
    public void removeNode(T node) {
        // Eliminar el nodo de las listas de adyacencia de otros nodos
        for (Set<T> neighbors : adjacencyList.values()) {
            neighbors.remove(node);
        }
        
        // Eliminar el nodo y su lista de adyacencia
        adjacencyList.remove(node);
    }
    
    /**
     * Elimina una conexión entre dos nodos
     * @param node1 Primer nodo
     * @param node2 Segundo nodo
     */
    public void removeEdge(T node1, T node2) {
        if (adjacencyList.containsKey(node1) && adjacencyList.containsKey(node2)) {
            adjacencyList.get(node1).remove(node2);
            adjacencyList.get(node2).remove(node1);
        }
    }
    
    /**
     * Obtiene todos los nodos del grafo
     * @return Set con todos los nodos
     */
    public Set<T> getAllNodes() {
        return adjacencyList.keySet();
    }
    
    /**
     * Encuentra el camino más corto entre dos nodos utilizando BFS
     * @param start Nodo inicial
     * @param end Nodo final
     * @return Lista con el camino más corto o lista vacía si no hay camino
     */
    public List<T> shortestPath(T start, T end) {
        if (!adjacencyList.containsKey(start) || !adjacencyList.containsKey(end)) {
            return Collections.emptyList();
        }
        
        Queue<T> queue = new LinkedList<>();
        Map<T, T> parentMap = new HashMap<>();
        Set<T> visited = new HashSet<>();
        
        queue.add(start);
        visited.add(start);
        
        while (!queue.isEmpty()) {
            T current = queue.poll();
            
            if (current.equals(end)) {
                // Reconstruir el camino
                return reconstructPath(parentMap, start, end);
            }
            
            for (T neighbor : adjacencyList.get(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }
        
        return Collections.emptyList(); // No se encontró camino
    }
    
    /**
     * Reconstruye el camino desde el nodo final hasta el inicial
     * @param parentMap Mapa de padres
     * @param start Nodo inicial
     * @param end Nodo final
     * @return Lista con el camino reconstruido
     */
    private List<T> reconstructPath(Map<T, T> parentMap, T start, T end) {
        LinkedList<T> path = new LinkedList<>();
        T current = end;
        
        while (current != null) {
            path.addFirst(current);
            current = parentMap.get(current);
            
            if (current != null && current.equals(start)) {
                path.addFirst(start);
                break;
            }
        }
        
        return path;
    }
    
    /**
     * Detecta clusters o comunidades en el grafo
     * @return Lista de clusters (cada cluster es un conjunto de nodos)
     */
    public List<Set<T>> detectCommunities() {
        List<Set<T>> communities = new ArrayList<>();
        Set<T> unvisited = new HashSet<>(adjacencyList.keySet());
        
        while (!unvisited.isEmpty()) {
            T startNode = unvisited.iterator().next();
            Set<T> community = new HashSet<>();
            
            // BFS para encontrar la comunidad conectada
            Queue<T> queue = new LinkedList<>();
            queue.add(startNode);
            community.add(startNode);
            unvisited.remove(startNode);
            
            while (!queue.isEmpty()) {
                T current = queue.poll();
                
                for (T neighbor : adjacencyList.get(current)) {
                    if (unvisited.contains(neighbor)) {
                        queue.add(neighbor);
                        community.add(neighbor);
                        unvisited.remove(neighbor);
                    }
                }
            }
            
            communities.add(community);
        }
        
        return communities;
    }
    
    /**
     * Calcula el grado (número de conexiones) de un nodo
     * @param node Nodo a consultar
     * @return Grado del nodo o 0 si el nodo no existe
     */
    public int getDegree(T node) {
        return adjacencyList.getOrDefault(node, Collections.emptySet()).size();
    }
}