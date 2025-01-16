package school.faang.user_service.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.MessageError;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillValidatorTest {
    private static final long ID = 1L;

    @InjectMocks
    private SkillValidator skillValidator;

    @Mock
    private SkillRepository skillRepository;


    @Test
    public void shouldSuccessValidate(){
        SkillDto skillDto = new SkillDto(ID, "title", List.of());

        when(skillRepository.existsByTitle(skillDto.title())).thenReturn(false);

        Assertions.assertDoesNotThrow(() -> skillValidator.validateSkill(skillDto));

        verify(skillRepository).existsByTitle(skillDto.title());
    }

    @Test
    public void shouldNullSkillIsInvalid() {
        Assertions.assertThrows(NullPointerException.class,
                () -> skillValidator.validateSkill(null));
        verifyNoInteractions(skillRepository);
    }

    @Test
    public void shouldNullTitleIsInvalid() {
        SkillDto skillDto = new SkillDto(ID, null, null);
        Assertions.assertThrows(NullPointerException.class,
                () -> skillValidator.validateSkill(skillDto));
        verifyNoInteractions(skillRepository);
    }

    @Test
    public void shouldBlankTitleIsInvalid() {
        SkillDto skillDto = new SkillDto(ID, "  ", null);
        DataValidationException exception = Assertions.assertThrows(DataValidationException.class,
                () -> skillValidator.validateSkill(skillDto));

        Assertions.assertEquals(MessageError.TITLE_BLANK.name(), exception.getMessage());
        verifyNoInteractions(skillRepository);
    }

    @Test
    public void shouldExistsByTitleIsInvalid() {
        SkillDto skillDto = new SkillDto(ID, "title", null);

        when(skillRepository.existsByTitle(skillDto.title())).thenReturn(true);

        DataValidationException exception = Assertions.assertThrows(DataValidationException.class,
                () -> skillValidator.validateSkill(skillDto));

        Assertions.assertEquals(MessageError.SKILL_TITLE_EXIST.name(), exception.getMessage());

        verify(skillRepository, Mockito.times(1))
                .existsByTitle(skillDto.title());
    }
}