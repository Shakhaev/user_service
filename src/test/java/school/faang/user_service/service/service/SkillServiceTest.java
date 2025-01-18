package school.faang.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validador.SkillValidator;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    private static final long USER_ID = 1L;
    private static final long SKILL_ID = 1L;

    @Mock
    private SkillValidator skillValidator;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillService skillService;

    private SkillDto skillDto;
    private User user;
    private Skill skill;

    @BeforeEach
    public void beforeEach() {
        skillDto = new SkillDto();
        skillDto.setTitle("Java");

        user = new User();
        user.setId(USER_ID);
        user.setUsername("Alex");

        skill = new Skill();
        skill.setId(SKILL_ID);
        skill.setTitle("Java");
    }

    @Test
    public void testCreate() {

        doNothing().when(skillValidator).validateSkill(skill);
        when(skillRepository.save(skill)).thenReturn(skill);

        Skill createdSkill = skillService.create(skill);

        assertEquals("Java", createdSkill.getTitle());
        verify(skillValidator, times(1)).validateSkill(skill);
        verify(skillRepository, times(1)).save(skill);
    }

    @Test
    public void testGetUserSkillsById() {
        when(skillRepository.findAllByUserId(USER_ID)).thenReturn(List.of(new Skill()));
        skillService.getUserSkills(USER_ID);

        verify(skillRepository, times(1)).findAllByUserId(USER_ID);
    }

    @Test
    public void testGetOfferedSkillsSuccessCase() {

        when(skillRepository.findSkillsOfferedToUser(USER_ID)).thenReturn(List.of(skill));
        skillService.getOfferedSkills(USER_ID);

        verify(skillRepository, times(1)).findSkillsOfferedToUser(USER_ID);
    }

    @Test
    void acquireSkillFromOffers_WhenSkillNotFound_ThrowsNoSuchElementException() {

        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID));
    }

}