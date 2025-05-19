package com.plataformaEducativa.proyectoestructuradatos.datastructure;


/**
 * Implementación de una Lista Enlazada para el historial de contenidos,
 * valoraciones y grupos de estudio.
 * @param <T> Tipo de elementos en la lista
 */
public class ListaEnlazada<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;
    
    private static class Node<T> {
        T data;
        Node<T> next;
        
        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }
    
    public ListaEnlazada() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }
    
    /**
     * Añade un elemento al final de la lista
     * @param data Elemento a añadir
     */
    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        
        size++;
    }
    
    /**
     * Añade un elemento al inicio de la lista
     * @param data Elemento a añadir
     */
    public void addFirst(T data) {
        Node<T> newNode = new Node<>(data);
        
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.next = head;
            head = newNode;
        }
        
        size++;
    }
    
    /**
     * Obtiene el elemento en una posición específica
     * @param index Posición del elemento
     * @return Elemento en la posición especificada
     * @throws IndexOutOfBoundsException si el índice está fuera de rango
     */
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Índice: " + index + ", Tamaño: " + size);
        }
        
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        
        return current.data;
    }
    
    /**
     * Elimina el elemento en una posición específica
     * @param index Posición del elemento a eliminar
     * @return Elemento eliminado
     * @throws IndexOutOfBoundsException si el índice está fuera de rango
     */
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Índice: " + index + ", Tamaño: " + size);
        }
        
        T removed;
        
        if (index == 0) {
            removed = head.data;
            head = head.next;
            
            if (head == null) {
                tail = null;
            }
        } else {
            Node<T> previous = getNodeAt(index - 1);
            Node<T> current = previous.next;
            removed = current.data;
            previous.next = current.next;
            
            if (current == tail) {
                tail = previous;
            }
        }
        
        size--;
        return removed;
    }
    
    /**
     * Obtiene el nodo en una posición específica
     * @param index Posición del nodo
     * @return Nodo en la posición especificada
     */
    private Node<T> getNodeAt(int index) {
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }
    
    /**
     * Verifica si la lista contiene un elemento específico
     * @param data Elemento a buscar
     * @return true si el elemento está en la lista, false en caso contrario
     */
    public boolean contains(T data) {
        Node<T> current = head;
        
        while (current != null) {
            if ((data == null && current.data == null) || 
                (data != null && data.equals(current.data))) {
                return true;
            }
            current = current.next;
        }
        
        return false;
    }
    
    /**
     * Elimina la primera ocurrencia de un elemento específico
     * @param data Elemento a eliminar
     * @return true si se eliminó correctamente, false si el elemento no existe
     */
    public boolean remove(T data) {
        if (head == null) {
            return false;
        }
        
        if ((data == null && head.data == null) || 
            (data != null && data.equals(head.data))) {
            head = head.next;
            size--;
            
            if (head == null) {
                tail = null;
            }
            
            return true;
        }
        
        Node<T> current = head;
        while (current.next != null) {
            if ((data == null && current.next.data == null) || 
                (data != null && data.equals(current.next.data))) {
                
                if (current.next == tail) {
                    tail = current;
                }
                
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        
        return false;
    }
    
    /**
     * @return Número de elementos en la lista
     */
    public int size() {
        return size;
    }
    
    /**
     * @return true si la lista está vacía, false en caso contrario
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Elimina todos los elementos de la lista
     */
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }
    
    /**
     * Implementación de iterador para recorrer la lista
     * @return Iterador de la lista
     */
    public ListIterator<T> iterator() {
        return new ListIterator<>(head);
    }
    
    /**
     * Clase interna para iterar sobre la lista
     * @param <T> Tipo de elementos en la lista
     */
    public static class ListIterator<T> {
        private Node<T> current;
        
        ListIterator(Node<T> head) {
            this.current = head;
        }
        
        /**
         * @return true si hay más elementos en la iteración
         */
        public boolean hasNext() {
            return current != null;
        }
        
        /**
         * @return Siguiente elemento en la iteración
         */
        public T next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            
            T data = current.data;
            current = current.next;
            return data;
        }
    }
}