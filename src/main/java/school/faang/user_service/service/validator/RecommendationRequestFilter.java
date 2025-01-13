package school.faang.user_service.service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecommendationRequestFilter {

    private final List<RecommendationRequestCondition> conditions;

    public List<RecommendationRequest> filterRequests(List<RecommendationRequest> allRequests, RequestFilterDto filter) {
        return allRequests.stream()
                .filter(request -> conditions.stream()
                        .allMatch(condition -> condition.matches(request, filter)))
                .collect(Collectors.toList());
    }
}
