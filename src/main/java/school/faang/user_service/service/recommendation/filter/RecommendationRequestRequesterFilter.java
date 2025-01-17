package school.faang.user_service.service.recommendation.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Component
public class RecommendationRequestRequesterFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.requesterId() != null;
    }

    @Override
    public boolean test(RecommendationRequest request, RequestFilterDto filters) {
        return request.getRequester().getId().equals(filters.requesterId());
    }
}
