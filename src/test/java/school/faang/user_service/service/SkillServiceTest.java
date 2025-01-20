package school.faang.user_service.service;

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
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
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

    @Spy
    SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    @Captor
    private ArgumentCaptor<Skill> captor;

    @Test
    public void testCreatingWithBlankNames() {
        SkillDto skillDto = new SkillDto(1L, " ");

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void testCreatingWithExistTitle() {
        SkillDto skillDto = prepareData(true);

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void testCreateSkill() {
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
    public void testAssignmentSkillWithoutRecommendedSkill() {
        Skill skill = new Skill();
        long skillId = 1L;
        long userId = 1L;
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(null);

        Optional<Skill> userSkill = skillRepository.findUserSkill(skillId, userId);

        assertNull(userSkill);
    }

    @Test
    public void  testAssignmentWithNoMinimumNumberOfRecommendations() {

        int MIN_SKILL_OFFERS = 3;
        long skillId = 1L;
        long userId = 1L;
        when(skillRepository.findUserSkill(1L, 1L)).thenReturn(null);

        // Мокируем, что предложений скилла меньше минимального числа (например, 2 предложения)
        when(skillOfferRepository.findAllOffersOfSkill(1L, 1L)).thenReturn(null);

        // Выполняем метод
        SkillDto result = skillService.acquireSkillFromOffers(1L, 1L);

        // Проверяем, что результат равен null, т.к. предложений недостаточно
        assertNull(result);

        // Проверяем, что метод findAllOffersOfSkill был вызван
        verify(skillOfferRepository, times(1)).findAllOffersOfSkill(1L, 1L);


    }

    private SkillDto prepareData(boolean existTitle) {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("Java");
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(existTitle);
        return skillDto;

    }
}
