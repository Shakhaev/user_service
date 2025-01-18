package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillCandidateMapper {
    Skill toEntity(SkillCandidateDto skillCandidateDto);

    SkillDto toSkillDto(Skill skill);

    @Mapping(target = "offersAmount", expression = "java(0L)")
    SkillCandidateDto toSkillCandidateDto(Skill skill);
}
