package school.faang.user_service.service.validator.conditions;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.service.validator.RecommendationRequestCondition;

@Component
public class RequesterIdCondition implements RecommendationRequestCondition {
    @Override
    public boolean matches(RecommendationRequest request, RequestFilterDto filter) {
        return filter.getRequesterId() == null || request.getRequester().getId().equals(filter.getRequesterId());
    }
}
