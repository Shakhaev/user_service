package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationRequestValidator {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public boolean checkUsersExist(long requesterId, long receiverId) {
        userRepository.findById(requesterId).orElseThrow(() -> new DataValidationException("Requester not found"));
        userRepository.findById(receiverId).orElseThrow(() -> new DataValidationException("Receiver not found"));
        return true;
    }

    public boolean checkRequestWithinSixMonthsExist(long requesterId, long receiverId) {
        if (recommendationRequestRepository.existsRequestWithinSixMonths(requesterId, receiverId)) {
            throw new DataValidationException("You've already requested recommendation for this user " +
                    "in the last 6 months");
        }
        return true;
    }

    public boolean checkAllSkillsExist(List<Long> skillIds) {
        boolean allSkillExist = skillIds.stream().allMatch(skillRepository::existsById);
        if (!allSkillExist) {
            throw new DataValidationException("Not all skills exist");
        }
        return true;
    }

    public void validateRecommendationRequestStatus(RecommendationRequest request) {
        RequestStatus requestStatus = request.getStatus();
        if (requestStatus == RequestStatus.ACCEPTED) {
            throw new DataValidationException("Recommendation request is already accepted");
        }
        if (requestStatus == RequestStatus.REJECTED) {
            throw new DataValidationException("Recommendation request is already rejected");
        }
    }

    public void checkRecommendationRequestExists(Optional<RecommendationRequest> request) {
        if (request.isEmpty()) {
            throw new DataValidationException("Recommendation request not found");
        }
    }
}
