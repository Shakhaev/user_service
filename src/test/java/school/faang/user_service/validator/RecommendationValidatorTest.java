package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationValidatorTest {
    @InjectMocks
    private RecommendationValidator recommendationValidator;

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillRepository skillRepository;

    @Test
    public void validateRecommendation_ShouldThrowDataValidationExceptionWhenContentIsEmpty() {
        Recommendation recommendation = new Recommendation();
        recommendation.setContent("");
        assertThrows(DataValidationException.class, () ->
                recommendationValidator.validateRecommendation(recommendation));
    }

    @Test
    public void validateRecommendation_ShouldThrowDataValidationExceptionWhenContentIsBlank() {
        Recommendation recommendation = new Recommendation();
        recommendation.setContent("   ");
        assertThrows(DataValidationException.class, () ->
                recommendationValidator.validateRecommendation(recommendation));
    }

    @Test
    public void validateRecommendation_ShouldThrowDataValidationExceptionWhenLastRecommendationWasCreatedRecently() {
        User author = new User();
        User receiver = new User();
        author.setId(1L);
        receiver.setId(2L);

        Recommendation recommendation = new Recommendation();
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        recommendation.setContent("content");
        recommendation.setCreatedAt(LocalDateTime.now());

        Recommendation lastRecommendation = new Recommendation();
        lastRecommendation.setCreatedAt(LocalDateTime.of(2024, 12, 15, 12, 30));

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId())).thenReturn(Optional.of(lastRecommendation));

        assertThrows(DataValidationException.class, () ->
                recommendationValidator.validateRecommendation(recommendation));
    }

    @Test
    public void validateRecommendation_ShouldValidateSuccessfully() {
        User author = new User();
        User receiver = new User();
        author.setId(1L);
        receiver.setId(2L);

        Recommendation recommendation = new Recommendation();
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        recommendation.setContent("content");
        recommendation.setCreatedAt(LocalDateTime.now());

        Recommendation lastRecommendation = new Recommendation();
        lastRecommendation.setCreatedAt(LocalDateTime.of(2024, 6, 15, 12, 30));

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId())).thenReturn(Optional.of(lastRecommendation));

        assertDoesNotThrow(() -> recommendationValidator.validateRecommendation(recommendation));
    }

    @Test
    public void validateOfferedSkills_ShouldThrowDataValidationExceptionWhenSkillDoesNotExist() {
        List<Long> skillIds = List.of(1L, 2L);

        when(skillRepository.existsById(1L)).thenReturn(true);
        when(skillRepository.existsById(2L)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateOfferedSkills(skillIds));
    }

    @Test
    public void validateOfferedSkills_ShouldValidateSuccessfully() {
        List<Long> skillIds = List.of(1L, 2L);

        when(skillRepository.existsById(1L)).thenReturn(true);
        when(skillRepository.existsById(2L)).thenReturn(true);

        assertDoesNotThrow(() -> recommendationValidator.validateOfferedSkills(skillIds));
    }

}
