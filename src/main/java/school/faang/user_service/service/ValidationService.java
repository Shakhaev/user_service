package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ValidationService {
    private final SkillService skillService;

    public void validateSkill(SkillDto skillDto) {
        Objects.requireNonNull(skillDto);

        String title = skillDto.title();
        Objects.requireNonNull(title);

        if (title.isBlank()) {
            throw new DataValidationException("Название скила не может быть пустым");
        }

        if (skillService.existsByTitle(title)) {
            throw new DataValidationException("Скилл с таким названием уже существует");
        }
    }
}
