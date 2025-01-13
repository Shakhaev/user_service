package school.faang.user_service.service.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.SkillCreateDto;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SkillCreateDtoTest {
    public static final long USER_ID = 1L;
    public static final long SKILL_ID = 1L;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testNullTitleIsInvalid() {
        Set<ConstraintViolation<SkillCreateDto>> violations = validator.validate(new SkillCreateDto());

        assertFalse(violations.isEmpty());
        violations.forEach(violation -> {
            assertEquals("Title cannot be null or empty !", violation.getMessage());
        }
        );
    }

    @Test
    public void testBlankTitleIsInvalid() {
        SkillCreateDto skill = new SkillCreateDto();
        skill.setTitle("  ");
        Set<ConstraintViolation<SkillCreateDto>> violations = validator.validate(skill);

        assertFalse(violations.isEmpty());
        violations.forEach(violation -> {
            assertEquals("Title cannot be null or empty !", violation.getMessage());
        }
        );
    }
}
