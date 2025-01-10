package school.faang.user_service.service;

import school.faang.user_service.dto.recommendation.RecommendationDto;

import java.util.List;

public interface RecommendationService {
    RecommendationDto create(RecommendationDto recommendation);

    RecommendationDto update(RecommendationDto updated);

    void delete(Long id);

    List<RecommendationDto> getAllUserRecommendations(long receiverId);
}
