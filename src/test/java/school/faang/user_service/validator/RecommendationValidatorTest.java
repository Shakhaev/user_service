package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import school.faang.user_service.dto.recommendation.CreateRecommendationRequest;
import school.faang.user_service.dto.recommendation.UpdateRecommendationRequest;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class RecommendationValidatorTest {

    private RecommendationValidator recommendationValidator;

    @BeforeEach
    public void init() {
        recommendationValidator = new RecommendationValidator();
    }

    @Test
    public void validateRecommendationContentIsNotEmpty_forCreateRequest_ShouldThrowDataValidationExceptionWhenContentIsEmpty() {
        CreateRecommendationRequest createRequest = new CreateRecommendationRequest();
        createRequest.setContent("");

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateRecommendationContentIsNotEmpty(createRequest));
    }

    @Test
    public void validateRecommendationContentIsNotEmpty_forCreateRequest_ShouldThrowDataValidationExceptionWhenContentIsBlank() {
        CreateRecommendationRequest createRequest = new CreateRecommendationRequest();
        createRequest.setContent("   ");

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateRecommendationContentIsNotEmpty(createRequest));
    }

    @Test
    public void validateRecommendationContentIsNotEmpty_forCreateRequest_ShouldValidateSuccessfully() {
        CreateRecommendationRequest createRequest = new CreateRecommendationRequest();
        createRequest.setContent("content");

        assertDoesNotThrow(() -> recommendationValidator.validateRecommendationContentIsNotEmpty(createRequest));
    }

    @Test
    public void validateRecommendationContentIsNotEmpty_forUpdateRequest_ShouldThrowDataValidationExceptionWhenContentIsEmpty() {
        UpdateRecommendationRequest updateRequest = new UpdateRecommendationRequest();
        updateRequest.setContent("");

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateRecommendationContentIsNotEmpty(updateRequest));
    }

    @Test
    public void validateRecommendationContentIsNotEmpty_forUpdateRequest_ShouldThrowDataValidationExceptionWhenContentIsBlank() {
        UpdateRecommendationRequest updateRequest = new UpdateRecommendationRequest();
        updateRequest.setContent("   ");

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateRecommendationContentIsNotEmpty(updateRequest));
    }

    @Test
    public void validateRecommendationContentIsNotEmpty_forUpdateRequest_ShouldValidateSuccessfully() {
        UpdateRecommendationRequest updateRequest = new UpdateRecommendationRequest();
        updateRequest.setContent("content");

        assertDoesNotThrow(() -> recommendationValidator.validateRecommendationContentIsNotEmpty(updateRequest));
    }

    @Test
    public void validateLastRecommendationTime_ShouldThrowDataValidationExceptionWhenLastRecommendationWasCreatedRecently() {
        Recommendation lastRecommendation = new Recommendation();
        lastRecommendation.setCreatedAt(LocalDateTime.of(2024, 12, 15, 12, 30));
        LocalDateTime currentCreationTime = LocalDateTime.now();

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateLastRecommendationTime(lastRecommendation, currentCreationTime));
    }

    @Test
    public void validateLastRecommendationTime_ShouldValidateSuccessfully() {
        Recommendation lastRecommendation = new Recommendation();
        lastRecommendation.setCreatedAt(LocalDateTime.of(2024, 6, 15, 12, 30));
        LocalDateTime currentCreationTime = LocalDateTime.now();

        assertDoesNotThrow(() -> recommendationValidator.validateLastRecommendationTime(lastRecommendation, currentCreationTime));
    }

}
