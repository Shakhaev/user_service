package school.faang.user_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SkillCandidateDto {
    private SkillDto skill;
    private long offersAmount;
}
