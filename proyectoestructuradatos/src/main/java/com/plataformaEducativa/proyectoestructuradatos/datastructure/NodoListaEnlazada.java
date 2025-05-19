package com.plataformaEducativa.proyectoestructuradatos.datastructure;

public class NodoListaEnlazada<T> {
    T data;
    NodoListaEnlazada<T> next;

    NodoListaEnlazada(T data) {
        this.data = data;
        this.next = null;
    }
}
