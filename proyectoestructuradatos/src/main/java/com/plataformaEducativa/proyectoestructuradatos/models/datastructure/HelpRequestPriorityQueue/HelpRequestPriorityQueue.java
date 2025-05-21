package com.plataformaEducativa.proyectoestructuradatos.models.datastructure.HelpRequestPriorityQueue;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import com.plataformaEducativa.proyectoestructuradatos.models.HelpRequest;

/**
 * Cola de prioridad para gestionar solicitudes de ayuda según su urgencia.
 * Implementada como un montículo binario (binary heap).
 * Esta versión separa la estructura de cola de los nodos que contiene.
 */
public class HelpRequestPriorityQueue {

    @Getter
    private final List<HelpRequestNode> heap;

    public HelpRequestPriorityQueue() {
        this.heap = new ArrayList<>();
    }

    /**
     * Añade una solicitud de ayuda a la cola de prioridad.
     * 
     * @param request Solicitud de ayuda a añadir
     */
    public void enqueue(HelpRequest request) {
        HelpRequestNode node = new HelpRequestNode(request);
        heap.add(node);
        siftUp(heap.size() - 1);
    }

    /**
     * Extrae la solicitud de ayuda con mayor prioridad.
     * 
     * @return Solicitud de ayuda con mayor prioridad
     * @throws NoSuchElementException si la cola está vacía
     */
    public HelpRequest dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Priority queue is empty");
        }

        HelpRequest top = heap.get(0).getHelpRequest();

        // Mover el último elemento al principio y reducir el tamaño
        HelpRequestNode last = heap.remove(heap.size() - 1);

        if (!isEmpty()) {
            heap.set(0, last);
            siftDown(0);
        }

        return top;
    }

    /**
     * Devuelve la solicitud con mayor prioridad sin eliminarla.
     * 
     * @return Solicitud de ayuda con mayor prioridad
     * @throws NoSuchElementException si la cola está vacía
     */
    public HelpRequest peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Priority queue is empty");
        }
        return heap.get(0).getHelpRequest();
    }

    /**
     * Verifica si la cola está vacía.
     * 
     * @return true si la cola está vacía, false en caso contrario
     */
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    /**
     * Devuelve el número de solicitudes en la cola.
     * 
     * @return Número de solicitudes
     */
    public int size() {
        return heap.size();
    }

    /**
     * Elimina una solicitud específica por su ID.
     * 
     * @param requestId ID de la solicitud a eliminar
     * @return true si la solicitud fue eliminada, false si no se encontró
     */
    public boolean remove(UUID requestId) {
        for (int i = 0; i < heap.size(); i++) {
            if (heap.get(i).getHelpRequest().getId().equals(requestId)) {
                // Reemplazar con el último elemento
                HelpRequestNode last = heap.remove(heap.size() - 1);

                // Si el elemento eliminado no era el último
                if (i < heap.size()) {
                    heap.set(i, last);

                    // Restablecer el orden del heap
                    siftUp(i);
                    siftDown(i);
                }

                return true;
            }
        }

        return false;
    }

    /**
     * Actualiza una solicitud existente y reajusta su posición en la cola.
     * 
     * @param updatedRequest Solicitud actualizada
     * @return true si la solicitud fue actualizada, false si no se encontró
     */
    public boolean update(HelpRequest updatedRequest) {
        for (int i = 0; i < heap.size(); i++) {
            if (heap.get(i).getHelpRequest().getId().equals(updatedRequest.getId())) {
                HelpRequest oldRequest = heap.get(i).getHelpRequest();
                heap.get(i).setHelpRequest(updatedRequest);

                // Determinar si la prioridad aumentó o disminuyó
                if (updatedRequest.compareTo(oldRequest) < 0) {
                    siftUp(i);
                } else {
                    siftDown(i);
                }

                return true;
            }
        }

        return false;
    }

    /**
     * Obtiene todas las solicitudes sin alterar el orden de la cola.
     * 
     * @return Lista de todas las solicitudes en la cola
     */
    public List<HelpRequest> getAllRequests() {
        List<HelpRequest> requests = new ArrayList<>();
        for (HelpRequestNode node : heap) {
            requests.add(node.getHelpRequest());
        }
        return requests;
    }

    /**
     * Busca una solicitud por su ID.
     * 
     * @param requestId ID de la solicitud a buscar
     * @return Solicitud encontrada o null si no existe
     */
    public HelpRequest findById(UUID requestId) {
        for (HelpRequestNode node : heap) {
            if (node.getHelpRequest().getId().equals(requestId)) {
                return node.getHelpRequest();
            }
        }
        return null;
    }

    /**
     * Hace flotar un elemento hacia arriba en el heap para mantener la propiedad de
     * heap.
     * 
     * @param index Índice del elemento a flotar
     */
    private void siftUp(int index) {
        int parentIndex;
        HelpRequestNode temp;

        if (index != 0) {
            parentIndex = (index - 1) / 2;

            if (heap.get(parentIndex).compareTo(heap.get(index)) > 0) {
                // Intercambiar con el padre
                temp = heap.get(index);
                heap.set(index, heap.get(parentIndex));
                heap.set(parentIndex, temp);

                // Continuar flotando hacia arriba
                siftUp(parentIndex);
            }
        }
    }

    /**
     * Hace hundir un elemento hacia abajo en el heap para mantener la propiedad de
     * heap.
     * 
     * @param index Índice del elemento a hundir
     */
    private void siftDown(int index) {
        int leftChild = 2 * index + 1;
        int rightChild = 2 * index + 2;
        int smallest = index;

        // Comprobar si el hijo izquierdo es menor
        if (leftChild < heap.size() && heap.get(leftChild).compareTo(heap.get(smallest)) < 0) {
            smallest = leftChild;
        }

        // Comprobar si el hijo derecho es menor
        if (rightChild < heap.size() && heap.get(rightChild).compareTo(heap.get(smallest)) < 0) {
            smallest = rightChild;
        }

        // Si el más pequeño no es el elemento actual, intercambiar y seguir hundiendo
        if (smallest != index) {
            HelpRequestNode temp = heap.get(index);
            heap.set(index, heap.get(smallest));
            heap.set(smallest, temp);

            siftDown(smallest);
        }
    }
}