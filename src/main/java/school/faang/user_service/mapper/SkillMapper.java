package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillCreateDto;
import school.faang.user_service.dto.skill.SkillReadDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {

    Skill toEntity(SkillCreateDto skillCreateDto);

    SkillReadDto toSkillDto(Skill skill);

    @Mapping(target = "offersAmount", expression = "java(0L)")
    SkillCandidateDto toSkillCandidateDto(Skill skill);
}
