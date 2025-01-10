package school.faang.user_service.service.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecommendationRequestFilter {

    public List<RecommendationRequest> filterRequests(List<RecommendationRequest> allRequests, RequestFilterDto filter) {
        return allRequests.stream()
                .filter(request -> isMatchingRequester(request, filter.getRequesterId()))
                .filter(request -> isMatchingReceiver(request, filter.getReceiverId()))
                .filter(request -> isMatchingStatus(request, filter.getStatus()))
                .filter(request -> isCreatedAfter(request, filter.getCreatedAfter()))
                .filter(request -> isCreatedBefore(request, filter.getCreatedBefore()))
                .collect(Collectors.toList());
    }

    private boolean isMatchingRequester(RecommendationRequest request, Long requesterId) {
        return requesterId == null || request.getRequester().getId().equals(requesterId);
    }

    private boolean isMatchingReceiver(RecommendationRequest request, Long receiverId) {
        return receiverId == null || request.getReceiver().getId().equals(receiverId);
    }

    private boolean isMatchingStatus(RecommendationRequest request, RequestStatus status) {
        return status == null || request.getStatus().equals(status);
    }

    private boolean isCreatedAfter(RecommendationRequest request, LocalDateTime createdAfter) {
        return createdAfter == null || request.getCreatedAt().isAfter(createdAfter);
    }

    private boolean isCreatedBefore(RecommendationRequest request, LocalDateTime createdBefore) {
        return createdBefore == null || request.getCreatedAt().isBefore(createdBefore);
    }
}
