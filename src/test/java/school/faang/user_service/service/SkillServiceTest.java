package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillMapper skillMapper;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @InjectMocks
    private SkillService skillService;

    private SkillDto skillDto;
    private Skill skill;

    @BeforeEach
    void setUp() {
        skillDto = new SkillDto();
        skillDto.setTitle("Java");

        skill = new Skill();
        skill.setTitle("Java");
    }

    @Test
    void testCreateSkill() {
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(false);
        when(skillMapper.toEntity(skillDto)).thenReturn(skill);
        when(skillRepository.save(skill)).thenReturn(skill);
        when(skillMapper.toDTO(skill)).thenReturn(skillDto);

        SkillDto result = skillService.create(skillDto);

        assertNotNull(result);
        assertEquals(skillDto.getTitle(), result.getTitle());
        verify(skillRepository, times(1)).existsByTitle(skillDto.getTitle());
        verify(skillRepository, times(1)).save(skill);
    }

    @Test
    void testCreateSkillThrowsExceptionWhenSkillExists() {
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> skillService.create(skillDto));
        verify(skillRepository, times(1)).existsByTitle(skillDto.getTitle());
        verify(skillRepository, never()).save(any(Skill.class));
    }

    @Test
    void testGetUserSkills() {
        long userId = 1L;
        when(skillRepository.findAllByUserId(userId)).thenReturn(Collections.singletonList(skill));
        when(skillMapper.toDTO(skill)).thenReturn(skillDto);

        List<SkillDto> result = skillService.getUserSkills(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(skillDto.getTitle(), result.get(0).getTitle());
        verify(skillRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    void testGetOfferedSkills() {
        long userId = 1L;
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(Collections.singletonList(skill));
        when(skillMapper.toDTO(skill)).thenReturn(skillDto);

        List<SkillCandidateDto> result = skillService.getOfferedSkills(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(skillDto.getTitle(), result.get(0).getSkill().getTitle());
        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
    }

    @Test
    void testAcquireSkillFromOffers() {
        long skillId = 1L;
        long userId = 1L;

        // Создаем объект Recommendation и устанавливаем автора
        Recommendation recommendation = new Recommendation();
        User author = new User();
        author.setId(2L); // Устанавливаем ID автора рекомендации
        recommendation.setAuthor(author);

        // Создаем объект SkillOffer и устанавливаем рекомендацию
        SkillOffer skillOffer = new SkillOffer();
        skillOffer.setRecommendation(recommendation);

        // Мокируем поведение репозиториев
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(Collections.nCopies(3, skillOffer));
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(skillMapper.toDTO(skill)).thenReturn(skillDto);

        // Вызываем метод и проверяем результат
        SkillDto result = skillService.acquireSkillFromOffers(skillId, userId);

        assertNotNull(result);
        assertEquals(skillDto.getTitle(), result.getTitle());
        verify(skillRepository, times(1)).findUserSkill(skillId, userId);
        verify(skillOfferRepository, times(1)).findAllOffersOfSkill(skillId, userId);
        verify(skillRepository, times(1)).assignSkillToUser(skillId, userId);
        verify(skillRepository, times(1)).findById(skillId);
    }

    @Test
    void testAcquireSkillFromOffersThrowsExceptionWhenSkillAlreadyExists() {
        long skillId = 1L;
        long userId = 1L;
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.of(skill));

        assertThrows(IllegalArgumentException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));
        verify(skillRepository, times(1)).findUserSkill(skillId, userId);
        verify(skillOfferRepository, never()).findAllOffersOfSkill(anyLong(), anyLong());
    }

    @Test
    void testAcquireSkillFromOffersThrowsExceptionWhenNotEnoughOffers() {
        long skillId = 1L;
        long userId = 1L;
        SkillOffer skillOffer = new SkillOffer();
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(Collections.nCopies(2, skillOffer));

        assertThrows(IllegalArgumentException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));
        verify(skillRepository, times(1)).findUserSkill(skillId, userId);
        verify(skillOfferRepository, times(1)).findAllOffersOfSkill(skillId, userId);
        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
    }
}