package school.faang.user_service.dto.skill;


import java.util.List;

public record SkillDto(long id,
                       String title,
                       List<Long> userIds) {
}
