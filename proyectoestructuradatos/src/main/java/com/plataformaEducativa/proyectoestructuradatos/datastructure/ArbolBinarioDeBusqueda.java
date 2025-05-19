package com.plataformaEducativa.proyectoestructuradatos.datastructure;

/**
 * Implementación de un Árbol Binario de Búsqueda para organizar contenidos.
 * 
 * @param <K> Tipo de la clave de comparación
 * @param <V> Tipo del valor almacenado
 */
public class ArbolBinarioDeBusqueda<K extends Comparable<K>, V> {
    private NodoArbolBinario<K, V> root;
    private int size;

    public ArbolBinarioDeBusqueda() {
        this.root = null;
        this.size = 0;
    }

    /**
     * Inserta un nuevo par clave-valor en el árbol
     * 
     * @param key   Clave de ordenamiento
     * @param value Valor a almacenar
     */
    public void insert(K key, V value) {
        root = insertRec(root, key, value);
        size++;
    }

    private NodoArbolBinario<K, V> insertRec(NodoArbolBinario<K, V> node, K key, V value) {
        if (node == null) {
            return new NodoArbolBinario<>(key, value);
        }

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            node.left = insertRec(node.left, key, value);
        } else if (cmp > 0) {
            node.right = insertRec(node.right, key, value);
        } else {
            // Si la clave ya existe, actualizamos el valor
            node.value = value;
            size--; // Compensar el incremento en el método público
        }

        return node;
    }

    /**
     * Busca un valor por su clave
     * 
     * @param key Clave a buscar
     * @return Valor asociado a la clave o null si no se encuentra
     */
    public V search(K key) {
        NodoArbolBinario<K, V> result = searchRec(root, key);
        return result == null ? null : result.value;
    }

    private NodoArbolBinario<K, V> searchRec(NodoArbolBinario<K, V> node, K key) {
        if (node == null || key.equals(node.key)) {
            return node;
        }

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            return searchRec(node.left, key);
        } else {
            return searchRec(node.right, key);
        }
    }

    /**
     * Elimina un elemento del árbol por su clave
     * 
     * @param key Clave del elemento a eliminar
     * @return true si se eliminó correctamente, false si la clave no existe
     */
    public boolean delete(K key) {
        int initialSize = size;
        root = deleteRec(root, key);
        return size < initialSize;
    }

    private NodoArbolBinario<K, V> deleteRec(NodoArbolBinario<K, V> node, K key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            node.left = deleteRec(node.left, key);
        } else if (cmp > 0) {
            node.right = deleteRec(node.right, key);
        } else {
            // Caso 1: Nodo hoja
            if (node.left == null && node.right == null) {
                size--;
                return null;
            }

            // Caso 2: Nodo con un hijo
            if (node.left == null) {
                size--;
                return node.right;
            }
            if (node.right == null) {
                size--;
                return node.left;
            }

            // Caso 3: Nodo con dos hijos
            // Encontrar el sucesor inorder (mínimo del subárbol derecho)
            NodoArbolBinario<K, V> successor = findMin(node.right);
            node.key = successor.key;
            node.value = successor.value;

            // Eliminar el sucesor
            node.right = deleteRec(node.right, successor.key);
        }

        return node;
    }

    private NodoArbolBinario<K, V> findMin(NodoArbolBinario<K, V> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    /**
     * Recorre el árbol en orden (inorder)
     * 
     * @param callback Función a ejecutar para cada nodo
     */
    public void inorderTraversal(TraversalCallback<K, V> callback) {
        inorderRec(root, callback);
    }

    private void inorderRec(NodoArbolBinario<K, V> node, TraversalCallback<K, V> callback) {
        if (node != null) {
            inorderRec(node.left, callback);
            callback.process(node.key, node.value);
            inorderRec(node.right, callback);
        }
    }

    /**
     * Interfaz para callbacks durante recorridos del árbol
     * 
     * @param <K> Tipo de la clave
     * @param <V> Tipo del valor
     */
    public interface TraversalCallback<K, V> {
        void process(K key, V value);
    }

    /**
     * @return Número de elementos en el árbol
     */
    public int size() {
        return size;
    }

    /**
     * @return true si el árbol está vacío, false en caso contrario
     */
    public boolean isEmpty() {
        return size == 0;
    }
}
