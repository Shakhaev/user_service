package school.faang.user_service.service.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapper;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private RecommendationMapper recommendationMapper = new RecommendationMapperImpl();

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
    public void createRecommendation_ShouldCallValidatorMethods() {
        doNothing().when(validator).validateMonthsBetweenRecommendations(recommendationDto);
        doNothing().when(validator).validateSkillOffers(recommendationDto);

        recommendationService.create(recommendationDto);

        verify(validator).validateMonthsBetweenRecommendations(recommendationDto);
        verify(validator).validateSkillOffers(recommendationDto);
    }

    @Test
    void createRecommendation_ShouldCreateWhenValidationPasses() {
        doNothing().when(validator).validateMonthsBetweenRecommendations(recommendationDto);
        doNothing().when(validator).validateSkillOffers(recommendationDto);
        when(skillRepository.findUserSkill(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(skillOfferRepository.create(anyLong(), anyLong())).thenReturn(id);

        RecommendationDto result = recommendationService.create(recommendationDto);

        assertEquals(recommendationDto, result);
        verify(validator).validateMonthsBetweenRecommendations(recommendationDto);
        verify(validator).validateSkillOffers(recommendationDto);
        verify(skillOfferRepository, times(2)).create(anyLong(), anyLong());
    }

    @Test
    public void updateRecommendation_ShouldUpdateWhenValidationPasses() {
        doNothing().when(validator).validateMonthsBetweenRecommendations(recommendationDto);
        doNothing().when(validator).validateSkillOffers(recommendationDto);

        RecommendationDto result = recommendationService.update(recommendationDto);

        assertEquals(recommendationDto, result);
        verify(validator).validateMonthsBetweenRecommendations(recommendationDto);
        verify(validator).validateSkillOffers(recommendationDto);
        verify(skillOfferRepository).deleteAllByRecommendationId(recommendationDto.id());
        verify(skillOfferRepository, times(2)).create(anyLong(), anyLong());
    }

    @Test
    public void deleteRecommendation_ShouldDeleteById() {
        recommendationService.delete(id);

        verify(recommendationRepository).deleteById(id);
    }

    @Test
    public void getAllUserRecommendations_ShouldReturnForGivenReceiverId() {
        Page<Recommendation> recommendations = new PageImpl<>(List.of(recommendation));
        when(recommendationRepository.findAllByReceiverId(anyLong(), Mockito.any())).thenReturn(recommendations);
        when(recommendationMapper.toDto(Mockito.any())).thenReturn(recommendationDto);
        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(id);

        assertEquals(1, result.size());
        verify(recommendationRepository).findAllByReceiverId(anyLong(), Mockito.any());
        verify(recommendationMapper).toDto(Mockito.any());
    }

    @Test
    void getAllGivenRecommendations_ShouldReturnRecommendationsForGivenAuthorId() {
        Page<Recommendation> recommendationPage = new PageImpl<>(List.of(recommendation));
        when(recommendationRepository.findAllByAuthorId(anyLong(), Mockito.any())).thenReturn(recommendationPage);
        when(recommendationMapper.toDto(Mockito.any())).thenReturn(recommendationDto);
        List<RecommendationDto> result = recommendationService.getAllGivenRecommendations(id);

        assertEquals(1, result.size());

        verify(recommendationRepository).findAllByAuthorId(anyLong(), Mockito.any());
        verify(recommendationMapper).toDto(Mockito.any());
    }
}

