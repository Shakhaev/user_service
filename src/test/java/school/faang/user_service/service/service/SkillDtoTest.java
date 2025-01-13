package school.faang.user_service.service.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.SkillDto;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SkillDtoTest {
    public static final long USER_ID = 1L;
    public static final long SKILL_ID = 1L;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testNullTitleIsInvalid() {
        Set<ConstraintViolation<SkillDto>> violations = validator.validate(new SkillDto());

        assertFalse(violations.isEmpty());
        violations.forEach(violation -> {
                    assertEquals("Title cannot be null or empty !", violation.getMessage());
                }
        );
    }

    @Test
    public void testBlankTitleIsInvalid() {
        SkillDto skill = new SkillDto();
        skill.setTitle("  ");
        Set<ConstraintViolation<SkillDto>> violations = validator.validate(skill);

        assertFalse(violations.isEmpty());
        violations.forEach(violation -> {
                    assertEquals("Title cannot be null or empty !", violation.getMessage());
                }
        );
    }
}
