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

import java.util.ArrayList;
import java.util.List;

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

    @Test
    @DisplayName("get skills by id - find")
    public void testListSkillsById() {
        SkillDto skill1 = new SkillDto();
        skill1.setId(1L);
        skill1.setTitle("Java");

        SkillDto skill2 = new SkillDto();
        skill2.setId(2L);
        skill2.setTitle("Spring");

        List<SkillDto> skills = new ArrayList<>();
        skills.add(skill1);
        skills.add(skill2);

        Mockito.when(skillService.getUserSkills(1L)).thenReturn((List<SkillDto>) List.of(skill1,skill2));
        List<SkillDto> skillDtos = skillController.getUserSkills(1l);

        assertEquals(skills, skillDtos);
    }

    @Test
    @DisplayName("get skills by id - not find")
    public void testEmptyListSkillsById() {
        List<SkillDto> skills = new ArrayList<>();
        Mockito.when(skillService.getUserSkills(1L)).thenReturn(new ArrayList<>());
        List<SkillDto> skillDtos = skillController.getUserSkills(1l);

        assertEquals(skills, skillDtos);
    }

}
