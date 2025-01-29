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
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    @Captor
    private ArgumentCaptor<Skill> captor;

    private final long skillId = 1L;
    private final long userId = 1L;

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
        Skill skill = new Skill();
        skill.setTitle(skillDto.getTitle());
        when(skillRepository.save(any(Skill.class))).thenReturn(skill);

        SkillDto dto = skillService.create(skillDto);

        verify(skillRepository, times(1)).save(captor.capture());
        Skill skillcaptor = captor.getValue();
        assertEquals(dto.getId(), skillcaptor.getId());
        assertEquals(dto.getTitle(), skillcaptor.getTitle());
    }

    @Test
    public void testGetUserSkills() {
        SkillDto dto = new SkillDto();
        dto.setId(2L);
        dto.setTitle("Java");
        Skill skill = new Skill();
        skill.setId(2L);
        skill.setTitle("Java");
        List<Skill> skills = List.of(skill);
        when(skillRepository.findAllByUserId(userId)).thenReturn(skills);

        List<SkillDto> userSkills = skillService.getUserSkills(userId);

        verify(skillRepository, times(1)).findAllByUserId(userId);

        assertEquals(1, userSkills.size());
        assertEquals(dto.getId(), userSkills.get(0).getId());
        assertEquals(dto.getTitle(), userSkills.get(0).getTitle());
    }

    @Test
    public void testGetOfferedSkills() {
        Skill skill = new Skill();
        List<Skill> skills = List.of(skill);
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(skills);

        skillService.getOfferedSkills(userId);

        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
    }

    @Test
    public void acquireSkillFromOffers_WithSkillNull() {
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(null);


        Optional<Skill> userSkill = skillRepository.findUserSkill(skillId, userId);

        assertNull(userSkill);
    }


    @Test
    public void acquireSkillFromOffers_SkillNotFound() {
        when(skillRepository.findById(skillId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));

        verify(skillRepository, times(1)).findById(skillId);

    }

    @Test
    void testAcquireSkillFromOffers_UserAlreadyHasSkill() {
        Skill skill = new Skill();
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(List.of(new SkillOffer()));

        assertThrows(IllegalArgumentException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));
    }

    @Test
    public void acquireSkillFromOffers_SuggestedLessThanTheStandardValue() {
        Skill skill = new Skill();
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId))
                .thenReturn(List.of(new SkillOffer()));

        assertThrows(IllegalArgumentException.class,
                () -> skillService.acquireSkillFromOffers(skillId, userId));
    }

    @Test
    public void acquireSkillFromOffers_verifyAccept() {

        Skill skill = new Skill();
        skill.setId(skillId);
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
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(offers);

        skillService.acquireSkillFromOffers(skillId, userId);

        verify(skillRepository, times(1)).assignSkillToUser(eq(skillId), eq(userId));

        ArgumentCaptor<UserSkillGuarantee> captor = ArgumentCaptor.forClass(UserSkillGuarantee.class);
        verify(userSkillGuaranteeRepository, times(3)).save(captor.capture());

        List<UserSkillGuarantee> capturedGuarantees = captor.getAllValues();
        assertNotNull(capturedGuarantees, "Captured guarantees list is null");
        assertEquals(3, capturedGuarantees.size(), "Unexpected number of guarantees saved");

    }

    private CreateSkillDto prepareData(boolean existTitle) {
        CreateSkillDto skillDto = new CreateSkillDto();
        skillDto.setTitle("Java");
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(existTitle);
        return skillDto;

    }
}
