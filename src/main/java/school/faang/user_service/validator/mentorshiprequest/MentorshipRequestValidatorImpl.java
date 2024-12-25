package school.faang.user_service.validator.mentorshiprequest;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.mentorshiprequest.MentorshipRequest;
import school.faang.user_service.entity.requeststatus.RequestStatus;
import school.faang.user_service.exception.data.DataValidationException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class MentorshipRequestValidatorImpl implements MentorshipRequestValidator {
    private static final int MONTHS_UNTIL_NEXT_REQUEST = 3;

    private final UserService userService;
    private final MentorshipRequestRepository mentorshipRequestRepository;

    @Override
    public void validateRequesterAndReceiver(Long requesterId, Long receiverId) {
        if (requesterId.equals(receiverId)) {
            throw new DataValidationException("requester and receiver must be different");
        }
        if (!userService.existsById(requesterId)) {
            throw new EntityNotFoundException("requester doesn't exist");
        }
        if (!userService.existsById(receiverId)) {
            throw new EntityNotFoundException("receiver doesn't exist");
        }
    }

    @Override
    public void validateRequestInterval(Long requesterId, Long receiverId) {
        mentorshipRequestRepository
                .findLatestRequest(requesterId, receiverId)
                .ifPresent(mentorshipRequest -> {
                    long monthsHavePassed = LocalDateTime.now().until(
                            mentorshipRequest.getCreatedAt(), ChronoUnit.MONTHS);

                    if (monthsHavePassed < MONTHS_UNTIL_NEXT_REQUEST) {
                        throw new DataValidationException(
                                String.format("you can send a request to one " +
                                                "receiver once every %d months",
                                        MONTHS_UNTIL_NEXT_REQUEST));
                    }
                });
    }

    @Override
    public MentorshipRequest getRequestByIdOrThrowException(Long requestId) {
        return mentorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("request doesn't exist"));
    }

    @Override
    public void validateRequestStatus(MentorshipRequest mentorshipRequest, RequestStatus requestedStatus) {
        if (mentorshipRequest.getStatus().equals(requestedStatus)) {
            throw new DataValidationException("the request has already been " + requestedStatus.name().toLowerCase());
        }
    }
}