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
import school.faang.user_service.exception.DataValidationException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    private final static int TEST_PERIOD_TO_ADD_NEW_RECOMMENDATION = 3;

    @InjectMocks
    private RecommendationService recommendationService;

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

    private RecommendationDto recommendationDto;
    private Recommendation recommendation;
    private User author;

    @BeforeEach
    public void setUp() {
        author = new User();
        recommendationDto = RecommendationDto.builder()
                .authorId(1L)
                .receiverId(2L)
                .content("content")
                .skillOffers(List.of(SkillOfferDto.builder()
                        .id(1L)
                        .skillId(1L)
                        .build()))
                .build();
        recommendation = recommendationMapper.toEntity(recommendationDto);
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
        Exception exception = assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendationDto));
        assertEquals("Вы предлагаете навыки, которых нет в системе", exception.getMessage());
    }

    @Test
    public void testCreateRecommendationSuccessful() {
        recommendation.setId(10L);
        recommendation.setAuthor(User.builder().id(1L).build());
        recommendation.setReceiver(User.builder().id(2L).build());

        when(userRepository.findById(recommendationDto.getAuthorId()))
                .thenReturn(Optional.ofNullable(User.builder().id(1L).build()));
        when(userRepository.findById(recommendationDto.getReceiverId()))
                .thenReturn(Optional.ofNullable(User.builder().id(2L).build()));
        when(skillOfferRepository.findAllByUserId(recommendationDto.getReceiverId()))
                .thenReturn(List.<SkillOffer>of(SkillOffer.builder()
                        .id(1L)
                        .skill(Skill.builder().id(1L).build())
                        .build()));
        when(skillOfferRepository.create(1L, 10L)).thenReturn(1L);
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);

        RecommendationDto result = recommendationService.create(recommendationDto);

        assertEquals(10L, result.getId());
    }

    @Test
    void testUpdateSuccessful() {
        recommendationDto.setId(10L);
        recommendation.setId(10L);
        when(recommendationRepository.findById(recommendationDto.getId())).thenReturn(Optional.of(recommendation));

        RecommendationDto result = recommendationService.update(recommendationDto);
        result.setContent("Updated content");

        verify(recommendationRepository, times(1))
                .update(anyLong(), anyLong(), anyString());

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Updated content", result.getContent());
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
        recommendation.setId(10L);
        when(recommendationRepository.existsById(recommendation.getId())).thenReturn(false);

        Exception result = assertThrows(DataValidationException.class, () ->
                recommendationService.delete(recommendation.getId()));

        assertEquals("Рекомендация с id:" + recommendation.getId() + " не найдена в системе",
                result.getMessage());
    }

    @Test
    void testDeleteSuccessful() {
        recommendation.setId(10L);
        when(recommendationRepository.existsById(recommendation.getId())).thenReturn(true);

        recommendationService.delete(recommendation.getId());

        verify(recommendationRepository, times(1)).deleteById(recommendation.getId());
    }
}
