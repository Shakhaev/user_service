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
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SkillCreateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.MinSkillOffersException;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.SkillService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

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

    @Captor
    private ArgumentCaptor<Skill> skillCaptor;

    @InjectMocks
    private SkillService skillService;

    private SkillDto skillDto;
    private User user;
    private Skill skill;
    private SkillCreateDto skillCreateDto;

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
    public void testCreateWithNullTitle() {
        Skill skill = new Skill();
        assertThrows(IllegalArgumentException.class, () -> skillService.create(skill));
    }

    @Test
    public void testCreateWithBlankTitle() {
        Skill skill = new Skill();
        skill.setTitle("");
        assertThrows(IllegalArgumentException.class, () -> skillService.create(skill));
    }

    @Test
    public void testCreateWithExistingTitle() {
        Skill skill = new Skill();
        skill.setTitle("title");

        when(skillRepository.existsByTitle(skill.getTitle())).thenReturn(true);

        Assert.assertThrows(DataValidationException.class, () -> skillService.create(skill)
        );
    }

    @Test
    public void testCreateWithSavesSkill() {
        when(skillRepository.existsByTitle("Java")).thenReturn(false);

        skillService.create(skill);
        Mockito.verify(skillRepository, Mockito.times(1)).save(skillCaptor.capture());

        Skill skill = skillCaptor.getValue();
        assertEquals(skillDto.getTitle(), skill.getTitle());
    }

    @Test
    public void testGetUserSkillsById() {
        Mockito.when(skillRepository.findAllByUserId(USER_ID)).thenReturn(List.of(new Skill()));
        skillService.getUserSkills(USER_ID);

        Mockito.verify(skillRepository, Mockito.times(1)).findAllByUserId(USER_ID);
    }

    @Test
    public void testGetOfferedSkillsSuccessCase() {

        Mockito.when(skillRepository.findSkillsOfferedToUser(USER_ID)).thenReturn(List.of(skill));
        skillService.getOfferedSkills(USER_ID);

        Mockito.verify(skillRepository, Mockito.times(1)).findSkillsOfferedToUser(USER_ID);

    }

    @Test
    public void testAcquireExistingSkillFromOffers() {
        Mockito.when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(skill));
        Mockito.when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.of(new Skill()));

        Assert.assertThrows(
                MinSkillOffersException.class,
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
                MinSkillOffersException.class,
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

        IntStream.range(0, minSkillOffers).forEach((i) -> {
            SkillOffer skillOffer = new SkillOffer();
            Recommendation recommendation = new Recommendation();
            recommendation.setAuthor(new User());
            skillOffer.setRecommendation(recommendation);
            skillOffers.add(skillOffer);
        });
        Mockito.when(skillOfferRepository.findAllOffersOfSkill(SKILL_ID, USER_ID)).thenReturn(skillOffers);

        skillService.acquireSkillFromOffers(SKILL_ID, USER_ID);

        Mockito.verify(skillRepository, Mockito.times(1)).assignSkillToUser(SKILL_ID, USER_ID);
        Mockito.verify(skillRepository, Mockito.times(1)).save(skill);
    }
}