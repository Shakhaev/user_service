package school.faang.user_service.dto.skill;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SkillCandidateDto {
    private Long skillId;
    private long offersAmount;
    //TODO Не понятно как использовать offersAmount в Mapper
}
