package school.faang.user_service.controller.recommendation;

import com.amazonaws.util.StringUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@Component
@AllArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;
    private final RecommendationRequestMapper recommendationRequestMapper;

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest == null || StringUtils.isNullOrEmpty(recommendationRequest.getMessage())) {
            throw new IllegalArgumentException("message must not be null");
        }

        return recommendationRequestMapper.toDto(recommendationRequestService.create(recommendationRequest));
    }

    public List<RecommendationRequest> getRecommendationRequests(RequestFilterDto filter) {
        return recommendationRequestService.getRequestByFilter(filter);
    }

    public RecommendationRequest getRecommendationRequest(long id) {
        return recommendationRequestService.getRequestById(id);
    }
}
