package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.user.UserSkillGuaranteeDto;
import school.faang.user_service.entity.UserSkillGuarantee;

@Mapper(componentModel = "spring")
public interface UserSkillGuaranteeMapper {
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "skillId", target = "skill.id")
    @Mapping(source = "guarantorId", target = "guarantor.id")
    UserSkillGuarantee toEntity(UserSkillGuaranteeDto userSkillGuaranteeDto);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "guarantor.id", target = "guarantorId")
    UserSkillGuaranteeDto toDto(UserSkillGuarantee userSkillGuarantee);

}
