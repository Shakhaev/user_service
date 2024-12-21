package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private SkillService skillService;
    private Skill firstSkill;
    private Skill secondSkill;


    @BeforeEach
    void setUp() {
        firstSkill = Skill.builder()
                .id(1L)
                .title("Java")
                .build();
        secondSkill = Skill.builder()
                .id(2L)
                .title("Python")
                .build();
    }

    @Test
    void getSkillsReturnSkillsWhenAllIdsExist() {
        List<Long> ids = List.of(firstSkill.getId(), secondSkill.getId());
        List<Skill> mockSkills = List.of(firstSkill, secondSkill);
        when(skillRepository.findAllByIds(ids)).thenReturn(mockSkills);

        List<Skill> result = skillService.getSkills(ids);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(skill -> skill.getTitle().equals("Java")));
        assertTrue(result.stream().anyMatch(skill -> skill.getTitle().equals("Python")));
        verify(skillRepository, times(1)).findAllByIds(ids);
    }

    @Test
    void getNonFullSkillsWhenMissingId() {
        List<Long> missingIds = List.of(1L, 2L, 3L);
        Skill missingSkill = Skill.builder()
                .id(3L)
                .build();
        List<Skill> mockSkills = List.of(firstSkill, secondSkill);
        when(skillRepository.findAllByIds(missingIds)).thenReturn(mockSkills);

        List<Skill> foundedSkills = skillService.getSkills(missingIds);

        assertEquals(2, foundedSkills.size());
        assertFalse(foundedSkills.contains(missingSkill));
        verify(skillRepository, times(1)).findAllByIds(missingIds);
    }

    @Test
    void getSkillsReturnEmptyListWhenIdsEmpty() {
        List<Long> ids = Collections.emptyList();

        List<Skill> result = skillService.getSkills(ids);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(skillRepository, never()).findAllByIds(ids);
    }
}
