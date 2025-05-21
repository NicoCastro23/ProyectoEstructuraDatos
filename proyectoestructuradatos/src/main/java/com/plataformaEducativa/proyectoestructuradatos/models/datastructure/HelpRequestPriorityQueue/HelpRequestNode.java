package com.plataformaEducativa.proyectoestructuradatos.models.datastructure.HelpRequestPriorityQueue;

import com.plataformaEducativa.proyectoestructuradatos.models.HelpRequest;

import lombok.Getter;
import lombok.Setter;

/**
 * Nodo para la Cola de Prioridad de Solicitudes de Ayuda.
 * Esta clase envuelve la solicitud de ayuda y añade funcionalidad
 * específica del nodo para la estructura de la cola de prioridad.
 */
@Getter
@Setter
public class HelpRequestNode implements Comparable<HelpRequestNode> {
    private HelpRequest helpRequest;

    public HelpRequestNode(HelpRequest helpRequest) {
        this.helpRequest = helpRequest;
    }

    /**
     * Compara este nodo con otro nodo basado en la prioridad y timestamp
     * de las solicitudes de ayuda.
     */
    @Override
    public int compareTo(HelpRequestNode other) {
        return this.helpRequest.compareTo(other.helpRequest);
    }
}