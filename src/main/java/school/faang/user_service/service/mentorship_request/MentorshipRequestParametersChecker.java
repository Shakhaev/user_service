package school.faang.user_service.service.mentorship_request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.exception.mentorship_request.EmptyMentorshipRequestDescriptionException;
import school.faang.user_service.exception.mentorship_request.MentorshipRequestWasAcceptedBeforeException;
import school.faang.user_service.exception.mentorship_request.NotEnoughTimeAfterLastRequestException;
import school.faang.user_service.exception.mentorship_request.UserRequestToHimselfException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentorshipRequestParametersChecker {
    @Value("${app.mentorship.request_for_mentoring_limit.times}")
    private int requestLimitTimes;

    @Value("${app.mentorship.request_for_mentoring_limit.period}")
    private int requestLimitPeriod;

    @Value("${app.mentorship.request_for_mentoring_limit.period_type}")
    private String requestLimitPeriodType;

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;

    public void checkExistAcceptedRequest(long requesterId, long receiverId) {
        boolean isExist = mentorshipRequestRepository.existAcceptedRequest(requesterId, receiverId);
        if (isExist) {
            log.error("Mentorship request from user with id {} to user with id {} was accepted before",
                    requesterId, receiverId);
            throw new MentorshipRequestWasAcceptedBeforeException(requesterId, receiverId);
        }
    }

    public void checkRequestParams(long requesterId, long receiverId, String description) {
        validateDescription(description);
        checkSameUserIds(requesterId, receiverId);
        checkExistUserId(requesterId);
        checkExistUserId(receiverId);
        checkLatestMentorshipRequest(requesterId, receiverId);
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            log.error("Description is null or empty");
            throw new EmptyMentorshipRequestDescriptionException();
        }
    }

    private void checkSameUserIds(long requesterId, long receiverId) {
        if (requesterId == receiverId) {
            log.error("User with id: {} cannot send a request to himself", requesterId);
            throw new UserRequestToHimselfException(requesterId);
        }
    }

    private void checkExistUserId(long id) {
        if (!userRepository.existsById(id)) {
            log.error("User with id {} not found", id);
            throw new UserNotFoundException(id);
        }
    }

    private void checkLatestMentorshipRequest(long requesterId, long receiverId) {
        MentorshipRequest latestMentorshipRequest = mentorshipRequestRepository
                .findLatestRequest(requesterId, receiverId)
                .orElse(null);
        LocalDateTime monthsAgo = LocalDateTime.now().minusMonths(requestLimitPeriod);

        if (latestMentorshipRequest != null && latestMentorshipRequest.getCreatedAt().isAfter(monthsAgo)) {
            log.error("A request for mentoring can be made only {} time every {} {}", requestLimitTimes,
                    requestLimitPeriod, requestLimitPeriodType);
            throw new NotEnoughTimeAfterLastRequestException("mentoring", requestLimitTimes,
                    requestLimitPeriod + " " + requestLimitPeriodType);
        }
    }
}
