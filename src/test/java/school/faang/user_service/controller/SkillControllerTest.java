package school.faang.user_service.controller;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;

import static org.junit.Assert.assertEquals;

public class SkillControllerTest {

    @Test
    @DisplayName("Check for empty title")
    public void testEmptyTitleIsValid() {
        SkillController skillController = new SkillController();
        SkillDto skill = new SkillDto();
        skill.setTitle("   ");
        Assert.assertThrows(DataValidationException.class, () -> skillController.create(skill));
    }

    @Test
    @DisplayName("Check for null title")
    public void testNullTitleIsValid() {
        SkillController skillController = new SkillController();
        SkillDto skill = new SkillDto();
        Assert.assertThrows(DataValidationException.class, () -> skillController.create(skill));
    }

    @Test
    @DisplayName("Check title is valid")
    public void testTitleIsValid() {
        SkillDto skill = new SkillDto();
        skill.setTitle("Java");

        assertEquals("Java", skill.getTitle());
    }
}
