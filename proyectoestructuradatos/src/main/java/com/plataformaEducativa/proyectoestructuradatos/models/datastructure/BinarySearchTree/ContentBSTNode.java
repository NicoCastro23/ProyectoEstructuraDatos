package com.plataformaEducativa.proyectoestructuradatos.models.datastructure.BinarySearchTree;

import com.plataformaEducativa.proyectoestructuradatos.models.Content;

import lombok.Getter;
import lombok.Setter;

/**
 * Nodo para el Árbol Binario de Búsqueda de Contenidos.
 * Esta clase representa cada nodo en la estructura del árbol.
 */
@Getter
@Setter
public class ContentBSTNode {
    private Content content;
    private ContentBSTNode left;
    private ContentBSTNode right;

    public ContentBSTNode(Content content) {
        this.content = content;
        this.left = null;
        this.right = null;
    }
}
