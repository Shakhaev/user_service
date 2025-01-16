package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.recommendation.*;

import java.util.List;

public interface RecommendationRequestService {

    CreateRecommendationRequestResponse create(CreateRecommendationRequestRequest recommendationRequest);

    List<GetRecommendationRequestResponse> getRequests(RequestFilterDto filter);

    GetRecommendationRequestResponse getRequest(long id);

    void rejectRequest(long id, RejectionDto rejection);
}
