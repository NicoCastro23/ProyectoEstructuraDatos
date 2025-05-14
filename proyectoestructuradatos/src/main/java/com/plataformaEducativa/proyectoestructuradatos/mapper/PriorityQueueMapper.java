package com.plataformaEducativa.proyectoestructuradatos.mapper;

import org.springframework.stereotype.Component;

import com.plataformaEducativa.proyectoestructuradatos.entity.ColaPrioridadAyudaEntity;
import com.plataformaEducativa.proyectoestructuradatos.models.HelpPriorityQueue;

@Component
public class PriorityQueueMapper {
    public HelpPriorityQueue toModel(ColaPrioridadAyudaEntity entity) {
        if (entity == null) {
            return null;
        }

        return HelpPriorityQueue.builder()
                .id(entity.getId())
                .tema(entity.getTema())
                .nivelUrgencia(entity.getNivelUrgencia())
                .fechaSolicitud(entity.getFechaSolicitud())
                .resuelta(entity.isResuelta())
                .descripcion(entity.getDescripcion())
                .build();

    }

    public ColaPrioridadAyudaEntity toEntity(HelpPriorityQueue model) {
        ColaPrioridadAyudaEntity entity = new ColaPrioridadAyudaEntity();
        entity.setId(model.getId());
        entity.setTema(model.getTema());
        entity.setNivelUrgencia(model.getNivelUrgencia());
        entity.setFechaSolicitud(model.getFechaSolicitud());
        entity.setResuelta(model.isResuelta());
        entity.setDescripcion(model.getDescripcion());
        // solicitudAyuda debe asignarse aparte si aplica
        return entity;
    }
}
