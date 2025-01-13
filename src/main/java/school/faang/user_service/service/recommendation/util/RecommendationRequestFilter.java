package school.faang.user_service.service.recommendation.util;

import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.function.Predicate;

public interface RecommendationRequestFilter {

    List<Predicate<RecommendationRequest>> getPredicates(RequestFilterDto filter);
}
