package school.faang.user_service.filter.recommendation;

import school.faang.user_service.dto.request.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.Filter;

public interface RecommendationRequestFilter
    extends Filter<RequestFilterDto, RecommendationRequest> {
}
