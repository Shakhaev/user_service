package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.entity.Skill;

@Mapper
public interface SkillMapper {
    SkillMapper INSTANCE = Mappers.getMapper(SkillMapper.class);

    @Mapping(target = "users", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "guarantees", ignore = true)
    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "events", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Skill toEntity(Long skillId);
}
