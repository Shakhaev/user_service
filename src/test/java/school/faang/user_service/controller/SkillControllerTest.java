package school.faang.user_service.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {
    public static final long USER_ID = 1L;
    public static final long SKILL_ID = 1L;

    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillController skillController;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testNullTitleIsInvalid() {
        Set<ConstraintViolation<SkillDto>> violations = validator.validate(new SkillDto());

        assertFalse(violations.isEmpty());
        violations.forEach(violation -> {
            assertEquals("Название умения не может быть пустым", violation.getMessage());
        });
    }

    @Test
    public void testBlankTitleIsInvalid() {
        SkillDto skill = new SkillDto();
        skill.setTitle("  ");
        Set<ConstraintViolation<SkillDto>> violations = validator.validate(skill);

        assertFalse(violations.isEmpty());
        violations.forEach(violation -> {
            assertEquals("Название умения не может быть пустым", violation.getMessage());
        });
    }

    @Test
    public void testOversizeTitleIsInvalid() {
        SkillDto skill = new SkillDto();
        skill.setTitle("AmazinglyUniqueAndLongStringForGeneratingRandomNamesInAConciseWay");
        Set<ConstraintViolation<SkillDto>> violations = validator.validate(skill);

        assertFalse(violations.isEmpty());
        violations.forEach(violation -> {
            assertEquals("Название умения должно быть не более 64 символов", violation.getMessage());
        });
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
        Mockito.when(skillService.getUserSkills(USER_ID)).thenReturn(null);

        assertNull(skillController.getUserSkills(USER_ID));
    }

    @Test
    public void testGetOfferedSkills() {
        Mockito.when(skillService.getOfferedSkills(USER_ID)).thenReturn(null);

        assertNull(skillController.getOfferedSkills(USER_ID));
    }

    @Test
    public void testAcquireSkillFromOffers() {
        Mockito.when(skillService.acquireSkillFromOffers(SKILL_ID, USER_ID)).thenReturn(null);

        assertNull(skillController.acquireSkillFromOffers(SKILL_ID, USER_ID));
    }
}
