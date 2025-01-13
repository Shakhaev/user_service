package school.faang.user_service.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {
    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillController skillController;

    SkillDto skill = new SkillDto();

    @Test
    @DisplayName("Check for empty title")
    public void testEmptyTitleIsValid() throws DataValidationException{
        skill.setId(1L);
        skill.setTitle("   ");

        assertThrows(DataValidationException.class, () -> skillController.create(skill));
    }

    @Test
    @DisplayName("Check for null title")
    public void testNullTitleIsValid() {
        assertThrows(DataValidationException.class, () -> skillController.create(skill));
    }

    @Test
    @DisplayName("Check title is valid")
    public void testTitleIsValid() {
        skill.setId(1L);
        skill.setTitle("Java");
        Mockito.when(skillService.create(any(SkillDto.class))).thenReturn(skill);
        SkillDto result = skillController.create(skill);

        assertEquals(skill.getTitle(), result.getTitle());
    }
}
