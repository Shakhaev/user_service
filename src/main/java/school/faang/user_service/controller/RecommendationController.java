package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequest;
import school.faang.user_service.dto.recommendation.CreateRecommendationResponse;
import school.faang.user_service.dto.recommendation.GetAllGivenRecommendationsResponse;
import school.faang.user_service.dto.recommendation.GetAllUserRecommendationsResponse;
import school.faang.user_service.dto.recommendation.UpdateRecommendationRequest;
import school.faang.user_service.dto.recommendation.UpdateRecommendationResponse;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    public CreateRecommendationResponse giveRecommendation(CreateRecommendationRequest recommendationRequest) {
        return recommendationService.create(recommendationRequest);
    }

    public UpdateRecommendationResponse updateRecommendation(UpdateRecommendationRequest recommendationRequest) {
        return recommendationService.update(recommendationRequest);
    }

    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    public List<GetAllUserRecommendationsResponse> getAllUserRecommendations(long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    public List<GetAllGivenRecommendationsResponse> getAllGivenRecommendations(long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }
}
