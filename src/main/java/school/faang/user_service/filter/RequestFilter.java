package school.faang.user_service.filter;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

public interface RequestFilter {

    boolean apply(RecommendationRequest recommendationRequest, RequestFilterDto requestFilterDto);
}
