package school.faang.user_service.validator.mentorshiprequest;

import school.faang.user_service.entity.mentorship.MentorshipRequest;
import school.faang.user_service.entity.requeststatus.RequestStatus;

public interface MentorshipRequestValidator {
    void validateRequesterAndReceiver(Long requesterId, Long receiverId);

    void validateRequestInterval(Long requesterId, Long receiverId);

    MentorshipRequest getRequestByIdOrThrowException(Long requestId);

    void validateRequestStatus(MentorshipRequest mentorshipRequest, RequestStatus requestedStatus);
}
