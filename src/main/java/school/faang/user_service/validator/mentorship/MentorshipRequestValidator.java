package school.faang.user_service.validator.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator {
    private final int REQUEST_FREQUENCY = 3;

    private final MentorshipRequestRepository requestRepository;
    private final UserRepository userRepository;

    public MentorshipRequest validateRequestId(long id) {
        try {
            return requestRepository.getReferenceById(id);
        } catch (EntityNotFoundException exception) {
            throw new EntityNotFoundException("Request not found");
        }
    }

    public boolean validateRequestForMentorship(long requesterId, long receiverId) {
        validateUsersIdNotEqual(requesterId, receiverId);
        validateUserExists(requesterId);
        validateUserExists(receiverId);
        validateLastRequestData(requesterId, receiverId);
        return true;
    }

    public boolean validateUsersIdNotEqual(long requesterId, long receiverId) {
        if (requesterId == receiverId) {
            throw new IllegalArgumentException("User can't request mentoring from yourself");
        } else {
            return true;
        }
    }

    public boolean validateUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found");
        } else {
            return true;
        }
    }

    public boolean validateLastRequestData(long requesterId, long receiverId) {
        Optional<MentorshipRequest> request = requestRepository.findLatestRequest(requesterId, receiverId);
        if (request.isPresent()) {
            LocalDateTime earliestTimeRequests = LocalDateTime.now().minusMonths(REQUEST_FREQUENCY);
            if (request.get().getCreatedAt().isBefore(earliestTimeRequests)) {
                throw new DataValidationException("Too early for next mentorship request");
            }
        }
        return true;
    }

    public boolean validateNotMentorYet(User requester, User receiver) {
        if (requester.getMentors().contains(receiver)) {
            throw new IllegalArgumentException("Requested mentor is already mentoring this user");
        } else {
            return true;
        }
    }
}
