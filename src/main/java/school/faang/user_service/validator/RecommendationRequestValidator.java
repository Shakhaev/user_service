package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
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

    public void checkUsersExist(long requesterId, long receiverId) {
        Optional<User> requester = userRepository.findById(requesterId);
        Optional<User> receiver = userRepository.findById(receiverId);

        if (requester.isEmpty()) {
            throw new DataValidationException("Requester not found");
        }
        if (receiver.isEmpty()) {
            throw new DataValidationException("Receiver not found");
        }
    }

    public void checkRequestWithinSixMonthsExist(long requesterId, long receiverId) {
        if (recommendationRequestRepository.existsRequestWithinSixMonths(requesterId, receiverId)) {
            throw new DataValidationException("You've already requested recommendation for this user "
                    + "in the last 6 months");
        }
    }

    public void checkAllSkillsExist(List<Long> skillIds) {
        boolean allSkillExist = skillIds.stream().allMatch(skillRepository::existsById);
        if (!allSkillExist) {
            throw new DataValidationException("Not all skills exist");
        }
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
