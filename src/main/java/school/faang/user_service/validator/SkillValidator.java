package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class SkillValidator {
    private final SkillRepository skillRepository;

    public void validateSkill(SkillDto skillDto) {
        Objects.requireNonNull(skillDto);

        String title = skillDto.title();
        Objects.requireNonNull(title);

        if (title.isBlank()) {
            throw new DataValidationException("Название скила не может быть пустым");
        }

        if (skillRepository.existsByTitle(title)) {
            throw new DataValidationException("Скилл с таким названием уже существует");
        }
    }
}
