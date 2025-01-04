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

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;

    public RecommendationRequest create(RecommendationRequest recommendationRequest) {
        validateRecommendationRequest(recommendationRequest);

        var savedRequest = recommendationRequestRepository.save(recommendationRequest);

        recommendationRequest.getSkills().forEach(skillRequest ->
                skillRequestRepository.create(savedRequest.getId(), skillRequest.getSkill().getId()));

        return savedRequest;
    }

//    public List<RecommendationRequest> getRequests(RequestFilterDto filter) {
//        return recommendationRequestRepository.findAll().stream()
//                .filter(request -> filter.getStatus() == null || request.getStatus() == filter.getStatus())
//                .filter(request -> filter.getRequesterId() == null || request.getRequesterId().equals(request.getRequester().getId()))
//                .filter(request -> filter.getReceiverId() == null || request.getReceiverId().equals(request.getReceiver().getId()))
//                .toList();
//    }

    public List<RecommendationRequest> getRequests(RequestFilterDto filter) {
        return recommendationRequestRepository.findAll().stream()
                .filter(request -> filter.getStatus() == null || request.getStatus() == filter.getStatus())
                .filter(request -> filter.getRequesterId() == null || filter.getRequesterId().equals(
                        request.getRequester() != null ? request.getRequester().getId() : null))
                .filter(request -> filter.getReceiverId() == null || filter.getReceiverId().equals(
                        request.getReceiver() != null ? request.getReceiver().getId() : null))
                .toList();
    }

    public RecommendationRequest getRequest(long id) {
        return recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recommendation request not found"));
    }

    public RecommendationRequest rejectRequest(long id, String reason) {
        var request = getRequest(id);
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

        var latestRequest = recommendationRequestRepository.findLatestPendingRequest(
                recommendationRequest.getRequester().getId(),
                recommendationRequest.getReceiver().getId()
        );

        latestRequest.ifPresent(request -> {
            if (request.getCreatedAt().plusMonths(6).isAfter(LocalDateTime.now())) {
                throw new IllegalStateException("Cannot request recommendation more than once in 6 months");
            }
        });
    }
}