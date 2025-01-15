package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.recommendation.RecommendationRequestResponseDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestCreateDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;

import java.util.List;

public interface RecommendationRequestService {

    RecommendationRequestResponseDto create(RecommendationRequestCreateDto recommendationRequest);

    List<RecommendationRequestResponseDto> getRequests(RequestFilterDto filter);

    RecommendationRequestResponseDto getRequest(long id);

    void rejectRequest(long id, RejectionDto rejection);
}
