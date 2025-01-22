package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import school.faang.user_service.dto.recommendation.CreateRecommendationRequest;
import school.faang.user_service.dto.recommendation.UpdateRecommendationRequest;
import school.faang.user_service.exception.DataValidationException;

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
}
