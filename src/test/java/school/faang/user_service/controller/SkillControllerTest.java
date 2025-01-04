package school.faang.user_service.controller;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.execption.DataValidationException;
import school.faang.user_service.service.SkillService;

import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {

    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillController skillController;

    @Test
    public void testNullTitleIsInvalid() {
        Assert.assertThrows(
                DataValidationException.class,
                () -> skillController.create(new SkillDto())
        );
    }

    @Test
    public void testBlankTitleIsInvalid() {
        SkillDto skill = new SkillDto();
        skill.setTitle("  ");
        Assert.assertThrows(
                DataValidationException.class,
                () -> skillController.create(skill)
        );
    }

    @Test
    public void testCreate() {
        SkillDto skill = new SkillDto();
        skill.setTitle("Java");
        Mockito.when(skillService.create(skill)).thenReturn(null);

        assertNull(skillController.create(skill));
    }

    @Test
    public void testGetUserSkills() {
        Mockito.when(skillService.getUserSkills(1L)).thenReturn(null);

        assertNull(skillController.getUserSkills(1L));
    }

    @Test
    public void testGetOfferedSkills() {
        Mockito.when(skillService.getOfferedSkills(1L)).thenReturn(null);

        assertNull(skillController.getOfferedSkills(1L));
    }

    @Test
    public void testAcquireSkillFromOffers() {
        Mockito.when(skillService.acquireSkillFromOffers(1L, 1L)).thenReturn(null);

        assertNull(skillController.acquireSkillFromOffers(1L, 1L));
    }
}
