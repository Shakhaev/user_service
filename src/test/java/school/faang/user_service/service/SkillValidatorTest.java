package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.Skill.SkillDto;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validator.skill.SkillValidator;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillValidatorTest {
    @InjectMocks
    private SkillValidator skillValidator;
    @Mock
    private SkillRepository skillRepository;

    @Test
    public void testValidateSkillWithBlankTitle() {
        SkillDto dto = preparedData(false);
        dto.setTitle(" ");

        Assertions.assertThrows(IllegalArgumentException.class, () -> skillValidator.validateSkill(dto));
    }

    @Test
    public void testValidateSkillWithExistingTitle() {
        SkillDto dto = preparedData(true);

        Assertions.assertThrows(IllegalArgumentException.class, () -> skillValidator.validateSkill(dto));
    }

    @Test
    public void testValidateSkill() {
        SkillDto dto = preparedData(false);

        Assertions.assertEquals(dto.getTitle(), "title");
    }

    private SkillDto preparedData(boolean titleIsExisting) {
        SkillDto dto = new SkillDto("title", 1L);
        when(skillRepository.existsByTitle(dto.getTitle())).thenReturn(titleIsExisting);
        return dto;
    }
}
