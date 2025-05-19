package com.plataformaEducativa.proyectoestructuradatos.datastructure;


import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * Implementación de una Cola de Prioridad para gestionar solicitudes de ayuda según urgencia.
 * @param <T> Tipo de elementos en la cola
 */
public class ColaDePrioridad<T> {
    private static final int INITIAL_CAPACITY = 10;
    private Object[] heap;
    private int size;
    private Comparator<? super T> comparator;
    
    public ColaDePrioridad() {
        this(null);
    }
    
    public PriorityQueue(Comparator<? super T> comparator) {
        this.heap = new Object[INITIAL_CAPACITY];
        this.size = 0;
        this.comparator = comparator;
    }
    
    /**
     * Añade un elemento a la cola de prioridad
     * @param element Elemento a añadir
     */
    public void add(T element) {
        if (size >= heap.length) {
            resize();
        }
        
        heap[size] = element;
        siftUp(size);
        size++;
    }
    
    /**
     * Obtiene y elimina el elemento de mayor prioridad
     * @return Elemento de mayor prioridad
     * @throws NoSuchElementException si la cola está vacía
     */
    @SuppressWarnings("unchecked")
    public T poll() {
        if (size == 0) {
            throw new NoSuchElementException("La cola de prioridad está vacía");
        }
        
        T result = (T) heap[0];
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;
        
        if (size > 0) {
            siftDown(0);
        }
        
        return result;
    }
    
    /**
     * Consulta el elemento de mayor prioridad sin eliminarlo
     * @return Elemento de mayor prioridad
     * @throws NoSuchElementException si la cola está vacía
     */
    @SuppressWarnings("unchecked")
    public T peek() {
        if (size == 0) {
            throw new NoSuchElementException("La cola de prioridad está vacía");
        }
        
        return (T) heap[0];
    }
    
    /**
     * @return Número de elementos en la cola
     */
    public int size() {
        return size;
    }
    
    /**
     * @return true si la cola está vacía, false en caso contrario
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Ajusta la posición de un elemento hacia arriba en el heap
     * @param index Índice del elemento a ajustar
     */
    @SuppressWarnings("unchecked")
    private void siftUp(int index) {
        T element = (T) heap[index];
        
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            T parent = (T) heap[parentIndex];
            
            if (compare(element, parent) <= 0) {
                break;
            }
            
            heap[index] = parent;
            index = parentIndex;
        }
        
        heap[index] = element;
    }
    
    /**
     * Ajusta la posición de un elemento hacia abajo en el heap
     * @param index Índice del elemento a ajustar
     */
    @SuppressWarnings("unchecked")
    private void siftDown(int index) {
        T element = (T) heap[index];
        int half = size / 2;
        
        while (index < half) {
            int leftChildIdx = 2 * index + 1;
            int rightChildIdx = leftChildIdx + 1;
            int maxChildIdx = leftChildIdx;
            
            // Determinar el índice del hijo con mayor prioridad
            if (rightChildIdx < size && 
                compare((T) heap[rightChildIdx], (T) heap[leftChildIdx]) > 0) {
                maxChildIdx = rightChildIdx;
            }
            
            // Si el elemento actual tiene mayor prioridad que ambos hijos, terminamos
            if (compare(element, (T) heap[maxChildIdx]) >= 0) {
                break;
            }
            
            // Mover el hijo hacia arriba
            heap[index] = heap[maxChildIdx];
            index = maxChildIdx;
        }
        
        heap[index] = element;
    }
    
    /**
     * Amplía la capacidad del array interno
     */
    private void resize() {
        Object[] newHeap = new Object[heap.length * 2];
        System.arraycopy(heap, 0, newHeap, 0, size);
        heap = newHeap;
    }
    
    /**
     * Compara dos elementos según el comparador especificado o el orden natural
     * @param a Primer elemento
     * @param b Segundo elemento
     * @return Resultado de la comparación
     */
    @SuppressWarnings("unchecked")
    private int compare(T a, T b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        } else {
            return ((Comparable<? super T>) a).compareTo(b);
        }
    }
}
