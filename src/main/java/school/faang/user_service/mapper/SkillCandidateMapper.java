package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillCandidateMapper {

    @Mapping(target = "id", source = "skillId")
    Skill toEntity(SkillCandidateDto skillCandidateDto);

    //TODO нужно разобраться как маппить SkillCandidateDto и откуда
    @Mapping(target = "skillId", source = "id")
    SkillCandidateDto toDto(Skill skill);

}
