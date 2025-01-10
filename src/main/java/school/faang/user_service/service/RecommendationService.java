package school.faang.user_service.service;

import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

public interface RecommendationService {
    RecommendationDto giveRecommendation(RecommendationDto recommendation);

    Recommendation updateRecommendation(RecommendationDto updated);
}
