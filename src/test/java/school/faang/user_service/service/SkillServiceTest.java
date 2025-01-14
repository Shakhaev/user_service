package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.AppConfig;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillMapper skillMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private AppConfig appConfig;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @InjectMocks
    private SkillService skillService;

    private Skill skill1;
    private Skill skill2;
    private SkillDto skillDto1;
    private SkillDto skillDto2;
    private User user;

    @BeforeEach
    void setUp() {
        skill1 = Skill.builder().id(1L).title("Java").build();
        skill2 = Skill.builder().id(2L).title("Python").build();
        skillDto1 = new SkillDto(1L, "Java");
        skillDto2 = new SkillDto(2L, "Python");
        user = User.builder().id(1L).username("John Doe").build();
    }

    @Test
    void testCreateSkill_Success() {
        CreateSkillDto createSkillDto = new CreateSkillDto("Java");
        Skill skillEntity = Skill.builder().id(1L).title("Java").build();
        SkillDto expectedSkillDto = new SkillDto(1L, "Java");

        when(skillRepository.existsByTitle("Java")).thenReturn(false);
        when(skillRepository.save(skillEntity)).thenReturn(skillEntity);
        when(skillMapper.toEntity(createSkillDto)).thenReturn(skillEntity);
        when(skillMapper.toDto(skillEntity)).thenReturn(expectedSkillDto);

        SkillDto actualSkillDto = skillService.create(createSkillDto);

        assertNotNull(actualSkillDto);
        assertEquals(expectedSkillDto.id(), actualSkillDto.id());
        assertEquals(expectedSkillDto.title(), actualSkillDto.title());

        verify(skillRepository, times(1)).existsByTitle("Java");
        verify(skillMapper, times(1)).toEntity(createSkillDto);
        verify(skillRepository, times(1)).save(skillEntity);
        verify(skillMapper, times(1)).toDto(skillEntity);
    }

    @Test
    void testCreateSkill_AlreadyExists() {
        CreateSkillDto createSkillDto = new CreateSkillDto("Java");

        when(skillRepository.existsByTitle("Java")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                skillService.create(createSkillDto)
        );

        assertEquals("Скилл уже существует", exception.getMessage());

        verify(skillRepository, times(1)).existsByTitle("Java");
        verify(skillMapper, never()).toEntity(any());
        verify(skillRepository, never()).save(any());
        verify(skillMapper, never()).toDto(any());
    }

    @Test
    void testGetUserSkills_WithSkills() {
        long userId = 1L;

        List<Skill> skills = Arrays.asList(skill1, skill2);
        when(skillRepository.findAllByUserId(userId)).thenReturn(skills);
        when(skillMapper.toDto(skill1)).thenReturn(skillDto1);
        when(skillMapper.toDto(skill2)).thenReturn(skillDto2);

        List<SkillDto> result = skillService.getUserSkills(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(skillDto1));
        assertTrue(result.contains(skillDto2));

        verify(skillRepository, times(1)).findAllByUserId(userId);
        verify(skillMapper, times(1)).toDto(skill1);
        verify(skillMapper, times(1)).toDto(skill2);
    }

    @Test
    void testGetUserSkills_NoSkills() {
        long userId = 1L;
        when(skillRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        List<SkillDto> result = skillService.getUserSkills(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(skillRepository, times(1)).findAllByUserId(userId);
        verify(skillMapper, never()).toDto(any());
    }

    @Test
    void testGetUserOfferedSkills_WithOffers() {
        long userId = 1L;

        List<Skill> offeredSkills = Arrays.asList(skill1, skill1, skill2); // skill1 предложен дважды, skill2 один раз

        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(offeredSkills);
        when(skillMapper.toDto(skill1)).thenReturn(skillDto1);
        when(skillMapper.toDto(skill2)).thenReturn(skillDto2);

        List<SkillCandidateDto> result = skillService.getUserOfferedSkills(userId);

        assertNotNull(result);
        assertEquals(2, result.size());

        SkillCandidateDto candidate1 = new SkillCandidateDto(skillDto1, 2L);
        SkillCandidateDto candidate2 = new SkillCandidateDto(skillDto2, 1L);

        assertTrue(result.contains(candidate1));
        assertTrue(result.contains(candidate2));

        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
        verify(skillMapper, times(2)).toDto(any(Skill.class));
    }

    @Test
    void testGetUserOfferedSkills_NoOffers() {
        long userId = 1L;
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(Collections.emptyList());

        List<SkillCandidateDto> result = skillService.getUserOfferedSkills(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
        verify(skillMapper, never()).toDto(any());
    }

    @Test
    @Transactional
    void testAcquireSkillFromOffers_Success() {
        long userId = 1L;
        long skillId = 1L;
        int minSkillOffers = 2;

        Skill skill = Skill.builder().id(skillId).title("Java").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));

        Recommendation recommendation1 = Recommendation.builder().author(new User()).build();
        SkillOffer offer1 = SkillOffer.builder().skill(skill).recommendation(recommendation1).build();

        Recommendation recommendation2 = Recommendation.builder().author(new User()).build();
        SkillOffer offer2 = SkillOffer.builder().skill(skill).recommendation(recommendation2).build();

        List<SkillOffer> skillOffers = Arrays.asList(offer1, offer2);
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(skillOffers);
        when(appConfig.getMinSkillOffers()).thenReturn(minSkillOffers);
        when(skillMapper.toDto(skill)).thenReturn(new SkillDto(skillId, "Java"));

        SkillDto result = skillService.acquireSkillFromOffers(skillId, userId);

        assertNotNull(result);
        assertEquals(skillId, result.id());
        assertEquals("Java", result.title());

        verify(userRepository, times(1)).findById(userId);
        verify(skillRepository, times(1)).findUserSkill(skillId, userId);
        verify(skillRepository, times(1)).findById(skillId);
        verify(skillOfferRepository, times(1)).findAllOffersOfSkill(skillId, userId);
        verify(skillRepository, times(1)).assignSkillToUser(skillId, userId);
        verify(userSkillGuaranteeRepository, times(2)).save(any(UserSkillGuarantee.class));
        verify(skillMapper, times(1)).toDto(skill);
    }

    @Test
    void testAcquireSkillFromOffers_UserNotFound() {
        long userId = 1L;
        long skillId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                skillService.acquireSkillFromOffers(skillId, userId)
        );

        assertEquals("Пользователь не найден", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(skillRepository, never()).findUserSkill(anyLong(), anyLong());
        verify(skillRepository, never()).findById(anyLong());
        verify(skillOfferRepository, never()).findAllOffersOfSkill(anyLong(), anyLong());
        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
        verify(userSkillGuaranteeRepository, never()).save(any());
        verify(skillMapper, never()).toDto(any());
    }

    @Test
    void testAcquireSkillFromOffers_SkillAlreadyExists() {
        long userId = 1L;
        long skillId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.of(new Skill())); // Навык уже есть

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                skillService.acquireSkillFromOffers(skillId, userId)
        );

        assertEquals("Такой скил уже есть у игрока", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(skillRepository, times(1)).findUserSkill(skillId, userId);
        verify(skillRepository, never()).findById(anyLong());
        verify(skillOfferRepository, never()).findAllOffersOfSkill(anyLong(), anyLong());
        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
        verify(userSkillGuaranteeRepository, never()).save(any());
        verify(skillMapper, never()).toDto(any());
    }

    @Test
    void testAcquireSkillFromOffers_SkillNotFound() {
        long userId = 1L;
        long skillId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillRepository.findById(skillId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                skillService.acquireSkillFromOffers(skillId, userId)
        );

        assertEquals("Скилл не существует", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(skillRepository, times(1)).findUserSkill(skillId, userId);
        verify(skillRepository, times(1)).findById(skillId);
        verify(skillOfferRepository, never()).findAllOffersOfSkill(anyLong(), anyLong());
        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
        verify(userSkillGuaranteeRepository, never()).save(any());
        verify(skillMapper, never()).toDto(any());
    }

    @Test
    void testAcquireSkillFromOffers_InsufficientOffers() {
        long userId = 1L;
        long skillId = 1L;
        int minSkillOffers = 3;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill1));

        Recommendation recommendation1 = Recommendation.builder().author(new User()).build();
        SkillOffer offer1 = SkillOffer.builder().skill(skill1).recommendation(recommendation1).build();

        List<SkillOffer> skillOffers = Collections.singletonList(offer1);
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(skillOffers);
        when(appConfig.getMinSkillOffers()).thenReturn(minSkillOffers);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                skillService.acquireSkillFromOffers(skillId, userId)
        );

        assertEquals("Не хватает рекомендаций. Минимальное количество: 3", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(skillRepository, times(1)).findUserSkill(skillId, userId);
        verify(skillRepository, times(1)).findById(skillId);
        verify(skillOfferRepository, times(1)).findAllOffersOfSkill(skillId, userId);
        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
        verify(userSkillGuaranteeRepository, never()).save(any());
        verify(skillMapper, never()).toDto(any());

        fail();
    }
}