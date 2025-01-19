package school.faang.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.validator.RecommendationServiceValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private RecommendationServiceValidator validator;

    @Spy
    private RecommendationMapperImpl recommendationMapper = new RecommendationMapperImpl();

    private RecommendationDto recommendationDto;
    private Recommendation recommendation;
    private long id;

    @BeforeEach
    public void init() {
        recommendationDto = RecommendationDto.builder()
                .id(1L)
                .skillOffers(List.of(new SkillOfferDto(2L, 1L), new SkillOfferDto(2L, 1L)))
                .authorId(5L)
                .receiverId(4L)
                .content("some content")
                .createdAt(LocalDateTime.now())
                .build();

        recommendation = Recommendation.builder()
                .id(1L)
                .author(new User())
                .receiver(new User())
                .content("some content")
                .createdAt(LocalDateTime.now())
                .build();

        id = 1L;
    }

    @Test
    public void createRecommendation_ShouldCallValidator() {
        doNothing().when(validator).validateMonthsBetweenRecommendations(recommendationDto);
        doNothing().when(validator).validateSkillOffers(recommendationDto);
        when(skillRepository.findUserSkill(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(skillOfferRepository.create(anyLong(), anyLong())).thenReturn(id);

        RecommendationDto result = recommendationService.create(recommendationDto);

        verify(validator).validateMonthsBetweenRecommendations(recommendationDto);
        verify(validator).validateSkillOffers(recommendationDto);

        assertEquals(recommendationDto, result);
    }

    @Test
    public void updateRecommendation_ShouldCallMapper() {
        doNothing().when(validator).validateMonthsBetweenRecommendations(recommendationDto);
        doNothing().when(validator).validateSkillOffers(recommendationDto);

        RecommendationDto result = recommendationService.update(recommendationDto);

        verify(validator).validateMonthsBetweenRecommendations(recommendationDto);
        verify(validator).validateSkillOffers(recommendationDto);

        verify(skillOfferRepository).deleteAllByRecommendationId(recommendationDto.id());
        verify(skillOfferRepository, times(2)).create(anyLong(), anyLong());

        assertEquals(recommendationDto, result);
    }

    @Test
    public void getAllUserRecommendations_ShouldCallMapperToDto() {
        Page<Recommendation> recommendations = new PageImpl<>(List.of(recommendation));
        when(recommendationRepository.findAllByReceiverId(anyLong(), any())).thenReturn(recommendations);

        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(id);

        verify(recommendationRepository).findAllByReceiverId(anyLong(), any());
        verify(recommendationMapper, times(1)).toDto(recommendation);

        assertEquals(1, result.size());
        assertEquals(recommendation.getContent(), result.get(0).content());
    }

    @Test
    public void getAllGivenRecommendations_ShouldCallMapperToDto() {
        Page<Recommendation> recommendationPage = new PageImpl<>(List.of(recommendation));
        when(recommendationRepository.findAllByAuthorId(anyLong(), any())).thenReturn(recommendationPage);

        List<RecommendationDto> result = recommendationService.getAllGivenRecommendations(id);

        verify(recommendationRepository).findAllByAuthorId(anyLong(), any());
        verify(recommendationMapper, times(1)).toDto(recommendation);

        assertEquals(1, result.size());
        assertEquals(recommendation.getContent(), result.get(0).content());
    }

    @Test
    public void deleteRecommendation_ShouldDeleteById() {
        recommendationService.delete(id);

        verify(recommendationRepository).deleteById(id);
        verifyNoInteractions(recommendationMapper);
    }
}
