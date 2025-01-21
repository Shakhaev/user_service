package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    @Spy
    SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    @Captor
    private ArgumentCaptor<Skill> captor;

    @Test
    public void create_CreatingWithBlankNames() {
        SkillDto skillDto = new SkillDto(1L, " ");

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void create_CreatingWithExistTitle() {
        SkillDto skillDto = prepareData(true);

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void create_CreateSkill() {
        SkillDto skillDto = prepareData(false);

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

    private final long SKILL_ID = 1L;
    private final long USER_ID = 1L;

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
        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.of(skill));

        SkillDto skillDto = skillService.acquireSkillFromOffers(SKILL_ID, USER_ID);

        assertNull(skillDto);
        verify(skillRepository, times(1)).findUserSkill(SKILL_ID, USER_ID);
    }

    @Test
    public void acquireSkillFromOffers_SuggestedLessThanTheStandardValue() {
        Skill skill = new Skill();
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(skill));
        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(null);
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

        SkillOffer offer1 = new SkillOffer();
        offer1.setSkill(skill);
        offer1.getSkill().setGuarantees(userSkillGuarantees);

        SkillOffer offer2 = new SkillOffer();
        offer2.setSkill(skill);
        offer2.getSkill().setGuarantees(userSkillGuarantees);

        SkillOffer offer3 = new SkillOffer();
        offer3.setSkill(skill);
        offer3.getSkill().setGuarantees(userSkillGuarantees);

        List<SkillOffer> offers = List.of(offer1, offer2, offer3);

        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(skill));
        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(null);
        when(skillOfferRepository.findAllOffersOfSkill(SKILL_ID, USER_ID)).thenReturn(offers);
        when(skillMapper.toDto(skill)).thenReturn(new SkillDto(SKILL_ID, "Test Skill"));

        SkillDto result = skillService.acquireSkillFromOffers(SKILL_ID, USER_ID);

        assertNotNull(result);
        assertEquals(SKILL_ID, result.getId());
        assertEquals("Test Skill", result.getTitle());

        verify(skillRepository, times(1)).assignSkillToUser(SKILL_ID, USER_ID);
        verify(skillRepository, times(offers.size())).save(skill);
        verify(skillMapper, times(1)).toDto(skill);
    }


    private SkillDto prepareData(boolean existTitle) {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("Java");
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(existTitle);
        return skillDto;

    }
}
