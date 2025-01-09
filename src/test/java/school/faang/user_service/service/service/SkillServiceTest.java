package school.faang.user_service.service.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SkillCreateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.SkillService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    private static final long USER_ID = 1L;
    private static final long SKILL_ID = 1L;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private SkillMapper skillMapper;

    @Spy
    private SkillCandidateMapper skillCandidateMapper;

    @Captor
    private ArgumentCaptor<Skill> skillCaptor;

    @InjectMocks
    private SkillService skillService;

    private SkillDto skillDto;
    private SkillCreateDto skillCreateDto;
    private User user;
    private Skill skill;

    @BeforeEach
    public void beforeEach() {
        skillCreateDto = new SkillCreateDto();
        skillCreateDto.setTitle("Java");

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
    public void testCreateWithExistingTitle() {
        Mockito.when(skillRepository.existsByTitle("Java")).thenReturn(true);

        Assert.assertThrows(
                BusinessException.class,
                () -> skillService.create(skillCreateDto)
        );
    }

    @Test
    public void testCreateSuccessCase() {
        Mockito.when(skillRepository.existsByTitle("Java")).thenReturn(false);

        skillService.create(skillCreateDto);
        Mockito.verify(skillRepository, Mockito.times(1)).save(skillCaptor.capture());

        Skill skill = skillCaptor.getValue();
        assertEquals(skillCreateDto.getTitle(), skill.getTitle());
    }
}
