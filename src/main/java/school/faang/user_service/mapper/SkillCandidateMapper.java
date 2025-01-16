package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring", uses = SkillMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillCandidateMapper {

    @Mapping(source = "skill", target = "skillDto")
    SkillCandidateDto toDto(Skill skill, long offersAmount);

    @Mapping(source = "skillDto", target = ".")
    Skill toEntity(SkillCandidateDto skillCandidateDto);
}
