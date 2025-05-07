package com.plataformaEducativa.proyectoestructuradatos.exception;

public class RecursoNoEncontradoException extends PlataformaEducativaException {

    public RecursoNoEncontradoException(String message) {
        super(message);
    }

    public RecursoNoEncontradoException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s no encontrado con %s: '%s'", resourceName, fieldName, fieldValue));
    }
}