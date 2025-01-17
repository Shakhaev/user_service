package school.faang.user_service.service.interfaces;

import jakarta.validation.constraints.NotNull;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;

import java.util.List;

public interface SkillServiceI {
    SkillDto create(CreateSkillDto skillDto);
    List<SkillDto> getUserSkills(Long userId);
    List<SkillCandidateDto> getUserOfferedSkills(@NotNull Long userId);
    SkillDto acquireSkillFromOffers(long skillId, long userId);
}
