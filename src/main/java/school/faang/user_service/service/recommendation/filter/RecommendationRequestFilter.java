package school.faang.user_service.service.recommendation.filter;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public interface RecommendationRequestFilter {
    boolean isApplicable(RequestFilterDto filters);

    boolean test(RecommendationRequest request, RequestFilterDto filters);
}
