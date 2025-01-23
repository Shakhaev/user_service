package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.RecommendationRejectionDto;
import school.faang.user_service.dto.recommendation.request.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.response.RecommendationResponseDto;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    public RecommendationResponseDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        return recommendationRequestService.createRecommendationRequest(recommendationRequest);
    }

    public List<RecommendationResponseDto> getRecommendationRequests(RecommendationRequestFilterDto filter) {
        return recommendationRequestService.getRecommendationRequests(filter);
    }

    public RecommendationResponseDto getRecommendationRequest(long recommendationRequestId) {
        return recommendationRequestService.getRecommendationRequest(recommendationRequestId);
    }

    public RecommendationResponseDto rejectRecommendationRequest(long recommendationRequestId,
                                                                 RecommendationRejectionDto rejection) {
        return recommendationRequestService.rejectRecommendationRequest(recommendationRequestId, rejection);
    }
}
