package school.faang.user_service.validator;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillValidatorTest {
    private static final long ID = 1L;

    @InjectMocks
    private SkillValidator skillValidator;

    @Mock
    private SkillRepository skillRepository;

    @Test
    public void shouldNullSkillIsInvalid() {
        Assert.assertThrows(NullPointerException.class,
                () -> skillValidator.validateSkill(null));
    }

    @Test
    public void shouldNullTitleIsInvalid() {
        SkillDto skillDto = new SkillDto(ID, null, null);
        Assert.assertThrows(NullPointerException.class,
                () -> skillValidator.validateSkill(skillDto));
    }

    @Test
    public void shouldBlankTitleIsInvalid() {
        SkillDto skillDto = new SkillDto(ID, "  ", null);
        Assert.assertThrows(DataValidationException.class,
                () -> skillValidator.validateSkill(skillDto));
    }

    @Test
    public void shouldExistsByTitleIsInvalid() {
        SkillDto skillDto = new SkillDto(ID, "title", null);

        skillValidator.validateSkill(skillDto);
        verify(skillRepository, Mockito.times(1))
                .existsByTitle(skillDto.title());
    }
}