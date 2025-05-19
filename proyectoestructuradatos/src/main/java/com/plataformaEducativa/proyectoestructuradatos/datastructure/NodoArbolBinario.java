package com.plataformaEducativa.proyectoestructuradatos.datastructure;

public class NodoArbolBinario<K, V> {
    K key;
    V value;
    NodoArbolBinario<K, V> left;
    NodoArbolBinario<K, V> right;

    NodoArbolBinario(K key, V value) {
        this.key = key;
        this.value = value;
        this.left = null;
        this.right = null;
    }
}
