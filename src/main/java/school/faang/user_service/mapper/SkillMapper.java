package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.Skill;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface SkillMapper {

    Skill toEntity(SkillDto skill);

    SkillDto toDto(Skill skill);

    SkillCandidateDto toCandidateDto (Skill skill);

}
