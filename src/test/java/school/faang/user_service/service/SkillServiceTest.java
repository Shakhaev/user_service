package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Spy
    private SkillMapperImpl skillMapper;

    @Captor
    private ArgumentCaptor<Skill> captor;

    private final long SKILL_ID = 1L;
    private final long USER_ID = 1L;

    @Test
    public void create_CreatingWithBlankNames() {
        CreateSkillDto skillDto = new CreateSkillDto(" ");

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void create_CreatingWithExistTitle() {
        CreateSkillDto skillDto = prepareData(true);

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void create_CreateSkill() {
        CreateSkillDto skillDto = prepareData(false);

        skillService.create(skillDto);

        verify(skillRepository, times(1)).save(captor.capture());
    }

    @Test
    public void testGetUserSkills() {
        long userId = 1L;
        Skill skill = new Skill();
        List<Skill> skills = List.of(skill);
        when(skillRepository.findAllByUserId(userId)).thenReturn(skills);

        skillService.getUserSkills(userId);

        verify(skillRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    public void testGetOfferedSkills() {
        long userId = 1L;
        Skill skill = new Skill();
        List<Skill> skills = List.of(skill);
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(skills);

        skillService.getOfferedSkills(userId);

        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
    }

    @Test
    public void acquireSkillFromOffers_WithSkillNull() {
        long skillId = 1L;
        long userId = 1L;
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(null);


        Optional<Skill> userSkill = skillRepository.findUserSkill(skillId, userId);

        assertNull(userSkill);
    }


    @Test
    public void acquireSkillFromOffers_SkillNotFound() {

        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID));

        verify(skillRepository, times(1)).findById(SKILL_ID);

    }

    @Test
    void testAcquireSkillFromOffers_UserAlreadyHasSkill() {
        Skill skill = new Skill();
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(skill));
        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(SKILL_ID, USER_ID)).thenReturn(List.of(new SkillOffer()));

        assertThrows(IllegalArgumentException.class, () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID));
    }

    @Test
    public void acquireSkillFromOffers_SuggestedLessThanTheStandardValue() {
        Skill skill = new Skill();
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(skill));
        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(SKILL_ID, USER_ID))
                .thenReturn(List.of(new SkillOffer()));

        assertThrows(IllegalArgumentException.class,
                () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID));
    }

    @Test
    public void acquireSkillFromOffers_verifyAccept() {
        Skill skill = new Skill();
        skill.setId(SKILL_ID);
        List<UserSkillGuarantee> userSkillGuarantees = List.of(new UserSkillGuarantee());
        skill.setGuarantees(userSkillGuarantees);

        SkillOffer offer1 = new SkillOffer();
        offer1.setSkill(skill);
        offer1.setRecommendation(new Recommendation());

        SkillOffer offer2 = new SkillOffer();
        offer2.setSkill(skill);
        offer2.setRecommendation(new Recommendation());

        SkillOffer offer3 = new SkillOffer();
        offer3.setSkill(skill);
        offer3.setRecommendation(new Recommendation());

        List<SkillOffer> offers = List.of(offer1, offer2, offer3);
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(skill));
        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(SKILL_ID, USER_ID)).thenReturn(offers);


        skillService.acquireSkillFromOffers(SKILL_ID, USER_ID);

        verify(skillRepository, times(1)).assignSkillToUser(SKILL_ID, USER_ID);
        verify(userSkillGuaranteeRepository, times(3)).save(Mockito.any());
    }


    private CreateSkillDto prepareData(boolean existTitle) {
        CreateSkillDto skillDto = new CreateSkillDto();
        skillDto.setTitle("Java");
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(existTitle);
        return skillDto;

    }
}
