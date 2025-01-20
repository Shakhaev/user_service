package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RecommendationRequestSkillFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RecommendationRequestFilterDto filters) {
        return filters.getSkillPattern() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequests,
                                               RecommendationRequestFilterDto filters) {
        return recommendationRequests.filter(request -> request.getSkills().stream()
                .anyMatch(skillRequest -> skillRequest.getSkill().getTitle().equals(filters.getSkillPattern())));
    }
}
