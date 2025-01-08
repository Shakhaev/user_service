package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCreateDto;
import school.faang.user_service.dto.skill.SkillReadDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.execption.BusinessException;
import school.faang.user_service.execption.DataValidationException;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
    private SkillMapperImpl skillMapper;

    @Captor
    private ArgumentCaptor<Skill> skillCaptor;

    @InjectMocks
    private SkillService skillService;

    private SkillReadDto skillReadDto;
    private SkillCreateDto skillCreateDto;
    private User user;
    private Skill skill;

    @BeforeEach
    public void beforeEach() {
        skillCreateDto = new SkillCreateDto();
        skillCreateDto.setTitle("Java");

        skillReadDto = new SkillReadDto();
        skillReadDto.setTitle("Java");

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

    @Test
    public void testGetUserSkillsSuccessCase() {
        Mockito.when(skillRepository.findAllByUserId(USER_ID)).thenReturn(List.of(new Skill()));
        skillService.getUserSkills(USER_ID);

        Mockito.verify(skillRepository, Mockito.times(1)).findAllByUserId(USER_ID);
    }

    @Test
    public void testGetOfferedSkillsSuccessCase() {
        Skill skill = skillMapper.toEntity(skillCreateDto);

        Mockito.when(skillRepository.findSkillsOfferedToUser(USER_ID)).thenReturn(List.of(skill));
        skillService.getOfferedSkills(USER_ID);

        Mockito.verify(skillRepository, Mockito.times(1)).findSkillsOfferedToUser(USER_ID);
        Mockito.verify(skillOfferRepository, Mockito.times(1)).findAllOffersOfSkill(skill.getId(), USER_ID);
    }

    @Test
    public void testAcquireExistingSkillFromOffers() {
        Mockito.when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(skill));
        Mockito.when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.of(new Skill()));

        Assert.assertThrows(
                BusinessException.class,
                () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID)
        );
    }

    @Test
    public void testAcquireSkillLessThanMinOffersNeeded() {
        Mockito.when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(skill));
        Mockito.when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.empty());
        Mockito.when(skillOfferRepository.findAllOffersOfSkill(SKILL_ID, USER_ID)).thenReturn(List.of(new SkillOffer()));

        Assert.assertThrows(
                BusinessException.class,
                () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID)
        );
    }

    @Test
    public void testAcquireNotExistingSkillFromOffers() {
        Mockito.when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(skill));

        Mockito.when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.empty());

        List<SkillOffer> skillOffers = new ArrayList<>();
        int minSkillOffers = SkillService.getMIN_SKILL_OFFERS();
        for (int i = 0; i < minSkillOffers; i++) {
            SkillOffer skillOffer = new SkillOffer();
            Recommendation recommendation = new Recommendation();
            recommendation.setAuthor(new User());
            skillOffer.setRecommendation(recommendation);
            skillOffers.add(skillOffer);
        }
        Mockito.when(skillOfferRepository.findAllOffersOfSkill(SKILL_ID, USER_ID)).thenReturn(skillOffers);

        skillService.acquireSkillFromOffers(SKILL_ID, USER_ID);

        Mockito.verify(skillRepository, Mockito.times(1)).assignSkillToUser(SKILL_ID, USER_ID);
        Mockito.verify(skillRepository, Mockito.times(1)).save(skill);
    }

}
