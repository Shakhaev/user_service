package school.faang.user_service.controller.recommendation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.BaseTest;
import school.faang.user_service.dto.RecommendationRequestDto;

public class RecommendationRequestControllerTest extends BaseTest {

    @Autowired
    private RecommendationRequestController recommendationRequestController;

    @Test
    public void requestRecommendation() {
        recommendationRequestController.requestRecommendation(new RecommendationRequestDto());
    }

    @Test
    void getRecommendationRequests() {
    }

    @Test
    void getRecommendationRequest() {
    }
}