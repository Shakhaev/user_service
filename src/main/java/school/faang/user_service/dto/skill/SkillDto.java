package school.faang.user_service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.Skill;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillDto {
    private long id;
    private String title;

}
