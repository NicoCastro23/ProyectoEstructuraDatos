package com.plataformaEducativa.proyectoestructuradatos.models.datastructure.BinarySearchTree;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.plataformaEducativa.proyectoestructuradatos.models.Content;

import lombok.Getter;

/**
 * Árbol Binario de Búsqueda para Contenidos educativos.
 * Esta implementación separa la estructura del árbol de sus nodos.
 */
public class ContentBinarySearchTree {

    @Getter
    private ContentBSTNode root;

    public ContentBinarySearchTree() {
        this.root = null;
    }

    /**
     * Inserta un nuevo contenido en el árbol.
     * 
     * @param content Contenido a insertar
     */
    public void insert(Content content) {
        root = insertRecursive(root, content);
    }

    private ContentBSTNode insertRecursive(ContentBSTNode current, Content content) {
        if (current == null) {
            return new ContentBSTNode(content);
        }

        // Using title for comparison in the BST
        int comparison = content.getTitle().compareToIgnoreCase(current.getContent().getTitle());

        if (comparison < 0) {
            current.setLeft(insertRecursive(current.getLeft(), content));
        } else if (comparison > 0) {
            current.setRight(insertRecursive(current.getRight(), content));
        } else {
            // For duplicate titles, we can either replace or ignore
            // Here we're replacing the content
            current.setContent(content);
        }

        return current;
    }

    /**
     * Busca un contenido por su título.
     * 
     * @param title Título a buscar
     * @return Contenido encontrado o null si no existe
     */
    public Content search(String title) {
        ContentBSTNode result = searchRecursive(root, title);
        return result != null ? result.getContent() : null;
    }

    private ContentBSTNode searchRecursive(ContentBSTNode current, String title) {
        if (current == null || current.getContent().getTitle().equalsIgnoreCase(title)) {
            return current;
        }

        if (title.compareToIgnoreCase(current.getContent().getTitle()) < 0) {
            return searchRecursive(current.getLeft(), title);
        } else {
            return searchRecursive(current.getRight(), title);
        }
    }

    /**
     * Busca contenidos que contengan una etiqueta específica.
     * 
     * @param tag Etiqueta a buscar
     * @return Lista de contenidos que contienen la etiqueta
     */
    public List<Content> searchByTag(String tag) {
        List<Content> result = new ArrayList<>();
        searchByTagRecursive(root, tag.toLowerCase(), result);
        return result;
    }

    private void searchByTagRecursive(ContentBSTNode current, String tag, List<Content> result) {
        if (current == null) {
            return;
        }

        // Check if current node's content has the tag
        if (current.getContent().getTags().stream().anyMatch(t -> t.toLowerCase().contains(tag))) {
            result.add(current.getContent());
        }

        // Continue searching in both subtrees
        searchByTagRecursive(current.getLeft(), tag, result);
        searchByTagRecursive(current.getRight(), tag, result);
    }

    /**
     * Busca contenidos por nombre de usuario del autor.
     * 
     * @param authorUsername Nombre de usuario del autor
     * @return Lista de contenidos del autor
     */
    public List<Content> searchByAuthor(String authorUsername) {
        List<Content> result = new ArrayList<>();
        searchByAuthorRecursive(root, authorUsername.toLowerCase(), result);
        return result;
    }

    private void searchByAuthorRecursive(ContentBSTNode current, String authorUsername, List<Content> result) {
        if (current == null) {
            return;
        }

        // Check if current node's content is by the author
        if (current.getContent().getAuthor().getUsername().toLowerCase().equals(authorUsername)) {
            result.add(current.getContent());
        }

        // Continue searching in both subtrees
        searchByAuthorRecursive(current.getLeft(), authorUsername, result);
        searchByAuthorRecursive(current.getRight(), authorUsername, result);
    }

    /**
     * Filtra contenidos según un predicado.
     * 
     * @param predicate Predicado para filtrar
     * @return Lista de contenidos que cumplen con el predicado
     */
    public List<Content> filter(Predicate<Content> predicate) {
        List<Content> result = new ArrayList<>();
        filterRecursive(root, predicate, result);
        return result;
    }

    private void filterRecursive(ContentBSTNode current, Predicate<Content> predicate, List<Content> result) {
        if (current == null) {
            return;
        }

        if (predicate.test(current.getContent())) {
            result.add(current.getContent());
        }

        filterRecursive(current.getLeft(), predicate, result);
        filterRecursive(current.getRight(), predicate, result);
    }

    /**
     * Realiza un recorrido en orden del árbol.
     * 
     * @return Lista de contenidos en orden alfabético por título
     */
    public List<Content> inOrderTraversal() {
        List<Content> result = new ArrayList<>();
        inOrderTraversalRecursive(root, result);
        return result;
    }

    private void inOrderTraversalRecursive(ContentBSTNode current, List<Content> result) {
        if (current == null) {
            return;
        }

        inOrderTraversalRecursive(current.getLeft(), result);
        result.add(current.getContent());
        inOrderTraversalRecursive(current.getRight(), result);
    }

    /**
     * Elimina un contenido por su título.
     * 
     * @param title Título del contenido a eliminar
     */
    public void remove(String title) {
        root = removeRecursive(root, title);
    }

    private ContentBSTNode removeRecursive(ContentBSTNode current, String title) {
        if (current == null) {
            return null;
        }

        int comparison = title.compareToIgnoreCase(current.getContent().getTitle());

        if (comparison < 0) {
            current.setLeft(removeRecursive(current.getLeft(), title));
        } else if (comparison > 0) {
            current.setRight(removeRecursive(current.getRight(), title));
        } else {
            // Node with only one child or no child
            if (current.getLeft() == null) {
                return current.getRight();
            } else if (current.getRight() == null) {
                return current.getLeft();
            }

            // Node with two children: Get the inorder successor (smallest in right subtree)
            current.setContent(findMin(current.getRight()).getContent());

            // Delete the inorder successor
            current.setRight(removeRecursive(current.getRight(), current.getContent().getTitle()));
        }

        return current;
    }

    private ContentBSTNode findMin(ContentBSTNode node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }
}