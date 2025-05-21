package com.plataformaEducativa.proyectoestructuradatos.mapper;

import com.plataformaEducativa.proyectoestructuradatos.dto.RegisterDto;
import com.plataformaEducativa.proyectoestructuradatos.dto.StudentDto;
import com.plataformaEducativa.proyectoestructuradatos.entity.StudentEntity;
import com.plataformaEducativa.proyectoestructuradatos.models.Student;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

/**
 * Interfaz para mapear entre StudentEntity, StudentDto y Student
 */
@Component
@Mapper(componentModel = "spring", uses = {})
public interface StudentMapper {

    @Mapping(target = "connectionCount", ignore = true)
    StudentDto entityToDto(StudentEntity entity);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "publishedContents", ignore = true)
    @Mapping(target = "studyGroups", ignore = true)
    @Mapping(target = "helpRequests", ignore = true)
    StudentEntity dtoToEntity(StudentDto dto);

    @Mapping(target = "connectionCount", ignore = true)
    Student entityToModel(StudentEntity entity);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "publishedContents", ignore = true)
    @Mapping(target = "studyGroups", ignore = true)
    @Mapping(target = "helpRequests", ignore = true)
    StudentEntity modelToEntity(Student model);

    @Mapping(target = "publishedContents", ignore = true)
    @Mapping(target = "studyGroups", ignore = true)
    @Mapping(target = "helpRequests", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(StudentDto dto, @MappingTarget StudentEntity entity);

    @Mapping(target = "role", defaultValue = "STUDENT")
    @Mapping(target = "publishedContents", ignore = true)
    @Mapping(target = "studyGroups", ignore = true)
    @Mapping(target = "helpRequests", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    StudentEntity registerDtoToEntity(RegisterDto registerDto);
}
