package com.plataformaEducativa.proyectoestructuradatos.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.plataformaEducativa.proyectoestructuradatos.dto.HelpRequestDto;
import com.plataformaEducativa.proyectoestructuradatos.entity.StudentEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.HelpRequestEntity;
import com.plataformaEducativa.proyectoestructuradatos.models.HelpRequest;
import com.plataformaEducativa.proyectoestructuradatos.repository.StudentRepository;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = { StudentMapper.class })
public abstract class HelpRequestMapper {

    @Autowired
    protected StudentRepository studentRepository;

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "requesterUsername", source = "requester.username")
    @Mapping(target = "helperId", source = "helper.id")
    @Mapping(target = "helperUsername", source = "helper.username")
    public abstract HelpRequestDto entityToDto(HelpRequestEntity entity);

    @Mapping(target = "requester", expression = "java(findStudentById(dto.getRequesterId()))")
    @Mapping(target = "helper", expression = "java(findStudentById(dto.getHelperId()))")
    public abstract HelpRequestEntity dtoToEntity(HelpRequestDto dto);

    @Mapping(target = "requester", source = "requester")
    @Mapping(target = "helper", source = "helper")
    public abstract HelpRequest entityToModel(HelpRequestEntity entity);

    @Mapping(target = "requester", source = "requester")
    @Mapping(target = "helper", source = "helper")
    public abstract HelpRequestEntity modelToEntity(HelpRequest model);

    @Mapping(target = "requester", expression = "java(findStudentById(dto.getRequesterId()))")
    @Mapping(target = "helper", expression = "java(findStudentById(dto.getHelperId()))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract void updateEntityFromDto(HelpRequestDto dto, @MappingTarget HelpRequestEntity entity);

    protected StudentEntity findStudentById(UUID id) {
        if (id == null) {
            return null;
        }
        return studentRepository.findById(id).orElse(null);
    }
}