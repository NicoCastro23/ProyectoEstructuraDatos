package com.plataformaEducativa.proyectoestructuradatos.exception;

import com.plataformaEducativa.proyectoestructuradatos.enums.TipoContenido;

public class TipoContenidoIncompatibleException extends PlataformaEducativaException {

    public TipoContenidoIncompatibleException(TipoContenido esperado, TipoContenido recibido) {
        super(String.format("Tipo de contenido incompatible. Se esperaba %s pero se recibió %s", esperado, recibido));
    }
}