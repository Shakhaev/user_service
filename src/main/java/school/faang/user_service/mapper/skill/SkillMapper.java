package school.faang.user_service.mapper.skill;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.skill.Skill;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {
    SkillDto toDto(Skill skill);
    List<SkillDto> toDto(List<Skill> skills);
    Skill toEntity(SkillDto skillDto);
}
