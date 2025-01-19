package school.faang.user_service.service;

import org.springframework.data.domain.Page;
import school.faang.user_service.dto.recommendation.RecommendationDto;

public interface RecommendationService {
    RecommendationDto create(RecommendationDto recommendation);

    public RecommendationDto update(RecommendationDto recommendation);

    void delete(long id);

    Page<RecommendationDto> getAllUserRecommendations(long receiverId, int page, int size);

    public Page<RecommendationDto> getAllGivenRecommendations(long authorId, int page, int size);
}


