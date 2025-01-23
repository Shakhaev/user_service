package school.faang.user_service.service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.validator.RecommendationServiceValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceValidatorTest {

    private final static int MONTHS_BEFORE_NEW_RECOMMENDATION = 6;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private RecommendationServiceValidator validator;

    private RecommendationDto recommendationDto;
    private long id;

    @BeforeEach
    public void init() {
        id = 1L;
        recommendationDto = RecommendationDto.builder()
                .id(1L)
                .receiverId(2L)
                .authorId(3L)
                .skillOffers(List.of(new SkillOfferDto(1L, 2L)))
                .createdAt(LocalDateTime.now())
                .content("rew")
                .build();
    }

    @Test
    public void validateMonthsBetweenRecommendationsShouldThrowExceptionWhenRecent() {
        Recommendation recommendationDb = Recommendation.builder()
                .id(2L)
                .createdAt(LocalDateTime.now())
                .build();
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenReturn(Optional.of(recommendationDb));

        assertThrows(DataValidationException.class, () -> validator.validateMonthsBetweenRecommendations(recommendationDto));
    }

    @Test
    public void validateMonthsBetweenRecommendationsShouldNotThrowExceptionWhenNoRecentRecommendation() {
        Recommendation recommendationFromDb = Recommendation.builder()
                .id(2L)
                .createdAt(LocalDateTime.now().minusMonths(MONTHS_BEFORE_NEW_RECOMMENDATION))
                .build();

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenReturn(Optional.of(recommendationFromDb));

        assertDoesNotThrow(() -> validator.validateMonthsBetweenRecommendations(recommendationDto));
    }

    @Test
    void validateSkillOffersShouldThrowExceptionWhenSkillDoesNotExist() {
        when(skillRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(DataValidationException.class, () -> validator.validateSkillOffers(recommendationDto));
    }

    @Test
    void validateSkillOffersShouldNotThrowExceptionWhenSkillsAreValidAndUnique() {
        when(skillRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(() -> validator.validateSkillOffers(recommendationDto));
    }
}
