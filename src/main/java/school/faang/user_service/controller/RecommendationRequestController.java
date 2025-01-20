package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        return recommendationRequestService.createRecommendationRequest(recommendationRequest);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RecommendationRequestFilterDto filter) {
        return recommendationRequestService.getRecommendationRequests(filter);
    }

    public RecommendationRequestDto getRecommendationRequest(long id) {
        return recommendationRequestService.getRecommendationRequest(id);
    }

    public RecommendationRequestDto rejectRecommendationRequest(long id, RejectionDto rejection) {
        return recommendationRequestService.rejectRecommendationRequest(id, rejection);
    }
}
