package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestSaveDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;

import java.util.List;

public interface RecommendationRequestService {

    RecommendationRequestDto create(RecommendationRequestSaveDto recommendationRequest);

    List<RecommendationRequestDto> getRequests(RequestFilterDto filter);
}
