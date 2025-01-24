package school.faang.user_service.dto.skill;

import lombok.Builder;

@Builder
public record CreateSkillDto(Long id, String title) { }
