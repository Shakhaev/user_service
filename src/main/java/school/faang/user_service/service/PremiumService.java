package school.faang.user_service.service;

import school.faang.user_service.dto.recommendation.RecommendationDto;

public interface PremiumService {
    Object buyPremium(Integer days);
}
