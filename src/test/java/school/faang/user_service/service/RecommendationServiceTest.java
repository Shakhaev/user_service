package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    private final static int TEST_PERIOD_TO_ADD_NEW_RECOMMENDATION = 3;

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private RecommendationMapperImpl recommendationMapper;

    @InjectMocks
    private RecommendationService recommendationService;

    private RecommendationDto recommendationDto;
    private Recommendation recommendation;
    private User author;
    private User receiver;

    @BeforeEach
    public void setUp() {
        author = new User();
        author.setId(1L);
        receiver = new User();
        receiver.setId(2L);

        recommendationDto = RecommendationDto.builder()
                .id(10L)
                .authorId(1L)
                .receiverId(2L)
                .content("content")
                .skillOffers(List.of(SkillOfferDto.builder()
                        .id(1L)
                        .skillId(1L)
                        .build()))
                .build();

        recommendation = recommendationMapper.toEntity(recommendationDto);
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        recommendation.setSkillOffers(List.of(SkillOffer.builder().id(1L).skill(Skill.builder().id(1L).build()).build()));
    }

    @Test
    public void testCreateValidatePeriodToAddNewRecommendation() {
        author.setId(recommendationDto.getAuthorId());
        recommendation.setAuthor(author);
        recommendation.setCreatedAt(LocalDateTime.now().minusMonths(TEST_PERIOD_TO_ADD_NEW_RECOMMENDATION));
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenReturn(Optional.of(recommendation));

        assertThrows(BusinessException.class, () -> recommendationService.create(recommendationDto));
    }

    @Test
    void testValidateSkillInSystemNotExists() {
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> recommendationService.create(recommendationDto));
        assertEquals("Вы предлагаете навыки, которых нет в системе", exception.getMessage());
    }

    @Test
    public void testCreateRecommendationSuccessful() {
        when(skillRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(recommendationDto.getAuthorId()))
                .thenReturn(Optional.ofNullable(author));
        when(userRepository.findById(recommendationDto.getReceiverId()))
                .thenReturn(Optional.ofNullable(receiver));
        when(recommendationRepository.existsById(recommendation.getId())).thenReturn(true);
        when(skillOfferRepository.findAllByUserId(recommendationDto.getReceiverId()))
                .thenReturn(List.of(SkillOffer.builder()
                        .id(1L)
                        .skill(Skill.builder().id(1L).build())
                        .build()));
        when(recommendationRepository.save(recommendation)).thenReturn(recommendation);

        RecommendationDto result = recommendationService.create(recommendationDto);

        verify(recommendationRepository, times(1)).save(recommendation);
        verify(skillOfferRepository, times(1)).create(recommendationDto.getSkillOffers().get(0).getSkillId(),
                recommendation.getId());

        assertNotNull(result);
        assertEquals(10L, result.getId());
    }

    @Test
    void testUpdateSuccessful() {
        when(recommendationRepository.existsById(recommendation.getId())).thenReturn(true);
        when(skillRepository.existsById(1L)).thenReturn(true);

        RecommendationDto result = recommendationService.update(recommendationDto);
        result.setContent("updated content");

        verify(recommendationRepository, times(1))
                .update(anyLong(), anyLong(), anyString());
        verify(skillOfferRepository, times(1)).deleteAllByRecommendationId(recommendationDto.getId());
        verify(skillOfferRepository, times(1)).create(recommendationDto.getSkillOffers().get(0).getSkillId(),
                recommendation.getId());

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("updated content", recommendationDto.getContent());
    }

    @Test
    void testGetAllUserRecommendationsFound() {
        Page<Recommendation> page = new PageImpl<>(List.of(recommendation));
        when(recommendationRepository.findAllByReceiverId(recommendationDto.getReceiverId(), Pageable.unpaged()))
                .thenReturn(page);
        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(recommendationDto.getReceiverId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("content", result.get(0).getContent());
    }

    @Test
    void testGetAllUserRecommendationsNotFound() {
        Page<Recommendation> page = new PageImpl<>(List.of());
        when(recommendationRepository.findAllByReceiverId(recommendationDto.getReceiverId(), Pageable.unpaged()))
                .thenReturn(page);
        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(recommendationDto.getReceiverId());

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testGetAllGivenRecommendationsFound() {
        Page<Recommendation> page = new PageImpl<>(List.of(recommendation));
        when(recommendationRepository.findAllByReceiverId(recommendationDto.getAuthorId(), Pageable.unpaged()))
                .thenReturn(page);
        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(recommendationDto.getAuthorId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("content", result.get(0).getContent());
    }

    @Test
    void testGetAllGivenRecommendationsNotFound() {
        Page<Recommendation> page = new PageImpl<>(List.of());
        when(recommendationRepository.findAllByReceiverId(recommendationDto.getAuthorId(), Pageable.unpaged()))
                .thenReturn(page);
        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(recommendationDto.getAuthorId());

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testDeleteWithValidateExistsById() {
        when(recommendationRepository.existsById(recommendation.getId())).thenReturn(false);

        Exception result = assertThrows(EntityNotFoundException.class, () ->
                recommendationService.delete(recommendation.getId()));

        assertEquals("Рекомендация с id:" + recommendation.getId() + " не найдена в системе",
                result.getMessage());
    }

    @Test
    void testDeleteSuccessful() {
        when(recommendationRepository.existsById(recommendation.getId())).thenReturn(true);

        recommendationService.delete(recommendation.getId());

        verify(recommendationRepository, times(1)).deleteById(recommendation.getId());
    }
}
