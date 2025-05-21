package com.plataformaEducativa.proyectoestructuradatos.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.plataformaEducativa.proyectoestructuradatos.dto.StudyGroupDto;
import com.plataformaEducativa.proyectoestructuradatos.entity.StudentEntity;
import com.plataformaEducativa.proyectoestructuradatos.entity.StudyGroupEntity;
import com.plataformaEducativa.proyectoestructuradatos.models.StudyGroup;
import com.plataformaEducativa.proyectoestructuradatos.repository.StudentRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = { StudentMapper.class })
public abstract class StudyGroupMapper {

    @Autowired
    protected StudentRepository studentRepository;

    @Mapping(target = "memberIds", expression = "java(getMemberIds(entity))")
    @Mapping(target = "memberUsernames", expression = "java(getMemberUsernames(entity))")
    public abstract StudyGroupDto entityToDto(StudyGroupEntity entity);

    @Mapping(target = "members", expression = "java(findStudentsByIds(dto.getMemberIds()))")
    public abstract StudyGroupEntity dtoToEntity(StudyGroupDto dto);

    @Mapping(target = "members", source = "members")
    public abstract StudyGroup entityToModel(StudyGroupEntity entity);

    @Mapping(target = "members", source = "members")
    public abstract StudyGroupEntity modelToEntity(StudyGroup model);

    @Mapping(target = "members", expression = "java(findStudentsByIds(dto.getMemberIds()))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract void updateEntityFromDto(StudyGroupDto dto, @MappingTarget StudyGroupEntity entity);

    protected Set<UUID> getMemberIds(StudyGroupEntity entity) {
        if (entity.getMembers() == null) {
            return new HashSet<>();
        }
        return entity.getMembers().stream()
                .map(StudentEntity::getId)
                .collect(Collectors.toSet());
    }

    protected Set<String> getMemberUsernames(StudyGroupEntity entity) {
        if (entity.getMembers() == null) {
            return new HashSet<>();
        }
        return entity.getMembers().stream()
                .map(StudentEntity::getUsername)
                .collect(Collectors.toSet());
    }

    protected Set<StudentEntity> findStudentsByIds(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        return ids.stream()
                .map(id -> studentRepository.findById(id).orElse(null))
                .filter(student -> student != null)
                .collect(Collectors.toSet());
    }
}
