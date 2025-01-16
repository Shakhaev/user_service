package school.faang.user_service.service.validator;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@FunctionalInterface
public interface RecommendationRequestCondition {
    boolean matches(RecommendationRequest request, RequestFilterDto requestFilterDto);
}
