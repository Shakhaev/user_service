package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;


@Component
@Mapper(componentModel = "spring", uses = SkillMapper.class)
public interface SkillCandidateMapper {

    SkillCandidateDto toDto(SkillDto skillDto, Long offersAmount);
}