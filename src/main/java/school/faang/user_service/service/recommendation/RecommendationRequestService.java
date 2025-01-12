package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestSaveDto;

public interface RecommendationRequestService {

    RecommendationRequestDto create(RecommendationRequestSaveDto recommendationRequest);
}
