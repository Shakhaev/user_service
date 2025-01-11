package school.faang.user_service.controller;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;

@Mapper(componentModel = "spring")
@Controller
public class SkillController {
    public SkillDto create(SkillDto skill) throws DataValidationException {
        validateSkill(skill);
        return skill;
    }

    public void validateSkill(SkillDto skill) throws DataValidationException {
        if (skill.getTitle() == null || skill.getTitle().isBlank()) {
            throw new DataValidationException("Title can't be empty or null!");
        }
    }
}
