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
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
        Set<Skill> mockSkills = Set.of(firstSkill, secondSkill);
        when(skillRepository.findAllByIds(ids)).thenReturn(mockSkills);

        List<Skill> result = skillService.getSkills(ids);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(skill -> skill.getTitle().equals("Java")));
        assertTrue(result.stream().anyMatch(skill -> skill.getTitle().equals("Python")));
        verify(skillRepository, times(1)).findAllByIds(ids);
    }

    @Test
    void getSkillsThrowExceptionWhenMissingId() {
        List<Long> missingIds = List.of(1L, 2L, 3L);
        Set<Skill> mockSkills = Set.of(firstSkill, secondSkill);
        when(skillRepository.findAllByIds(missingIds)).thenReturn(mockSkills);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> skillService.getSkills(missingIds));

        assertEquals("Not found skills with ids [3]", exception.getMessage());
        verify(skillRepository, times(1)).findAllByIds(missingIds);
    }

    @Test
    void getSkillsReturnEmptyListWhenIdsEmpty() {
        List<Long> ids = Collections.emptyList();
        when(skillRepository.findAllByIds(ids)).thenReturn(Collections.emptySet());

        List<Skill> result = skillService.getSkills(ids);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(skillRepository, times(1)).findAllByIds(ids);
    }

    @Test
    void getSkillsNotCallRepositoryWhenNullIds() {
        List<Long> ids = null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> skillService.getSkills(ids));
        assertEquals("Skill IDs must not be null", exception.getMessage());
        verify(skillRepository, never()).findAllByIds(any());
    }
}