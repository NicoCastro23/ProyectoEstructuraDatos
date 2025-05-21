package com.plataformaEducativa.proyectoestructuradatos.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.plataformaEducativa.proyectoestructuradatos.dto.ContentDto;
import com.plataformaEducativa.proyectoestructuradatos.entity.ContentEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.StudentEntity;
import com.plataformaEducativa.proyectoestructuradatos.models.Content;
import com.plataformaEducativa.proyectoestructuradatos.repository.StudentRepository;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = { StudentMapper.class })
public abstract class ContentMapper {

    @Autowired
    protected StudentRepository studentRepository;

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorUsername", source = "author.username")
    public abstract ContentDto entityToDto(ContentEntity entity);

    @Mapping(target = "author", expression = "java(findStudentById(dto.getAuthorId()))")
    @Mapping(target = "ratings", ignore = true)
    public abstract ContentEntity dtoToEntity(ContentDto dto);

    @Mapping(target = "author", source = "author")
    public abstract Content entityToModel(ContentEntity entity);

    @Mapping(target = "author", source = "author")
    @Mapping(target = "ratings", ignore = true)
    public abstract ContentEntity modelToEntity(Content model);

    @Mapping(target = "author", expression = "java(findStudentById(dto.getAuthorId()))")
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract void updateEntityFromDto(ContentDto dto, @MappingTarget ContentEntity entity);

    protected StudentEntity findStudentById(UUID id) {
        if (id == null) {
            return null;
        }
        return studentRepository.findById(id).orElse(null);
    }
}
