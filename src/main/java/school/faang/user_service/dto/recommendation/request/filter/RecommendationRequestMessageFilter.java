package school.faang.user_service.dto.recommendation.request.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.Objects;

@Component
public class RecommendationRequestMessageFilter implements RecommendationRequestFilter {

    @Override
    public boolean isApplicable(RecommendationRequestFilterDto requestFilterDto) {
        return requestFilterDto != null
                && requestFilterDto.getMessage() != null;
    }

    @Override
    public List<RecommendationRequest> apply(List<RecommendationRequest> recommendationRequests, RecommendationRequestFilterDto requestFilterDto) {
        return recommendationRequests.stream()
                .filter(request -> Objects.equals(request.getMessage(), requestFilterDto.getMessage()))
                .toList();
    }
}
