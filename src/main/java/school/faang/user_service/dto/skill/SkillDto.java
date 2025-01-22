package school.faang.user_service.dto.skill;

import lombok.Builder;

import java.util.List;

@Builder
public record SkillDto(long id,
                       String title,
                       List<Long> userIds) {
}
