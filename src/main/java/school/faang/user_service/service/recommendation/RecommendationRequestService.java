package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;

public interface RecommendationRequestService {
    RecommendationRequest create(RecommendationRequestDto recommendationRequest);

    List<RecommendationRequest> getRequestByFilter(RequestFilterDto filter);

    RecommendationRequest getRequestById(Long id);
}
