package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface SkillMapper {
    SkillDto toDto(Skill skill);
}
