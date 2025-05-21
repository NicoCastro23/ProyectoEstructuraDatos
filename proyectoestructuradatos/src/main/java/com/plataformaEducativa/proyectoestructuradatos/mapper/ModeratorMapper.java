package com.plataformaEducativa.proyectoestructuradatos.mapper;

import com.plataformaEducativa.proyectoestructuradatos.dto.ModeratorDto;
import com.plataformaEducativa.proyectoestructuradatos.dto.RegisterDto;
import com.plataformaEducativa.proyectoestructuradatos.entity.ModeratorEntity;
import com.plataformaEducativa.proyectoestructuradatos.models.Moderator;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

/**
 * Interfaz para mapear entre ModeratorEntity, ModeratorDto y Moderator
 */
@Component
@Mapper(componentModel = "spring", uses = {})
public interface ModeratorMapper {

    ModeratorDto entityToDto(ModeratorEntity entity);

    @Mapping(target = "password", ignore = true)
    ModeratorEntity dtoToEntity(ModeratorDto dto);

    Moderator entityToModel(ModeratorEntity entity);

    ModeratorEntity modelToEntity(Moderator model);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(ModeratorDto dto, @MappingTarget ModeratorEntity entity);

    @Mapping(target = "role", defaultValue = "MODERATOR")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    ModeratorEntity registerDtoToEntity(RegisterDto registerDto);
}