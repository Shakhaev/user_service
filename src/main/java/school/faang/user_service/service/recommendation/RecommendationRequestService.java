package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;

    public RecommendationRequest create(RecommendationRequest recommendationRequest) {
        validateRecommendationRequest(recommendationRequest);
        RecommendationRequest savedRequest = recommendationRequestRepository.save(recommendationRequest);
        skillRequestRepository.saveAll(savedRequest.getSkills());
        return savedRequest;
    }

    public List<RecommendationRequest> getRequests(RequestFilterDto filter) {
        return recommendationRequestRepository.findAll().stream()
                .filter(request -> filter.getStatus() == null || request.getStatus() == filter.getStatus())
                .filter(request -> filter.getRequesterId() == null || Objects.equals(
                        request.getRequester() != null ? request.getRequester().getId() : null, filter.getRequesterId()))
                .filter(request -> filter.getReceiverId() == null || Objects.equals(
                        request.getReceiver() != null ? request.getReceiver().getId() : null, filter.getReceiverId()))
                .toList();
    }

    public RecommendationRequest getRequest(long id) {
        return recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recommendation request not found"));
    }

    public RecommendationRequest rejectRequest(long id, String reason) {
        RecommendationRequest request = getRequest(id);
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Cannot reject a non-pending request");
        }
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(reason);
        return recommendationRequestRepository.save(request);
    }

    private void validateRecommendationRequest(RecommendationRequest recommendationRequest) {
        if (recommendationRequest.getMessage() == null || recommendationRequest.getMessage().isBlank()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        recommendationRequestRepository.findLatestPendingRequest(
                        recommendationRequest.getRequester().getId(), recommendationRequest.getReceiver().getId())
                .ifPresent(request -> {
                    if (request.getCreatedAt().plusMonths(6).isAfter(LocalDateTime.now())) {
                        throw new IllegalStateException("Cannot request recommendation more than once in 6 months");
                    }
                });
    }
}