package school.faang.user_service.validator;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestCreateDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.InvalidMentorshipRequestException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestValidator {
    private static final long SELF_REQUEST_MONTH_LIMIT = 3;
    private final MentorshipRequestRepository repository;
    private final UserValidator userValidator;

    public void validateMentorshipRequest(MentorshipRequestCreateDto dto) {
        validateSelfRequest(dto);
        validateOneRequestPerMonthLimit(dto);
        validateRequesterAndReceiverExists(dto);
    }

    public void validateMentorshipRequestExists(long id) {
        if (!repository.existsById(id)) {
            log.warn("Mentorship request with id #{} not exists.", id);
            throw new EntityNotFoundException("Mentorship request with id #" + id + " not exists.");
        }
        log.info("Mentorship request id #{} exists.", id);
    }

    public void validateRequesterHasReceiverAsMentor(User requester, User receiver) {
        if (requester.getMentors().contains(receiver)) {
            log.warn("Requester id #{} already has mentor id #{}.", requester.getId(), receiver.getId());
            throw new InvalidMentorshipRequestException("Requester id #" + requester.getId() +
                    " already has mentor id @" + receiver.getId() + ".");
        }
        log.info("UserId #{} has no UserId #{} as mentor.", requester.getId(), receiver.getId());
    }

    private void validateOneRequestPerMonthLimit(MentorshipRequestCreateDto dto) {
        repository.findLatestRequest(dto.getRequesterId(), dto.getReceiverId())
                .filter(request -> !request.getCreatedAt().isBefore(getMonthLimitAgo()))
                .ifPresent(request -> {
                    log.warn("Cannot request mentorship more then 1 time per {} months.", SELF_REQUEST_MONTH_LIMIT);
                    throw new InvalidMentorshipRequestException("Cannot request mentorship more then" +
                            " 1 time per " + SELF_REQUEST_MONTH_LIMIT + " months.");
                });
        log.info("UserId #{} did not request mentorship from UserId #{} in the last {} months.",
                dto.getRequesterId(), dto.getReceiverId(), SELF_REQUEST_MONTH_LIMIT);
    }

    private void validateRequesterAndReceiverExists(MentorshipRequestCreateDto dto) {
        userValidator.validateUserById(dto.getRequesterId());
        userValidator.validateUserById(dto.getReceiverId());
        log.info("Users #{} and #{} exist.", dto.getRequesterId(), dto.getReceiverId());
    }

    private void validateSelfRequest(MentorshipRequestCreateDto dto) {
        if (dto.getRequesterId().equals(dto.getReceiverId())) {
            log.warn("Self-mentorship requests are not allowed.");
            throw new InvalidMentorshipRequestException("Self-mentorship requests are not allowed.");
        }
        log.info("UserId #{} is not self-requesting mentorship.", dto.getRequesterId());
    }

    private LocalDateTime getMonthLimitAgo() {
        return LocalDateTime.now(ZoneId.of("UTC")).minusMonths(SELF_REQUEST_MONTH_LIMIT);
    }
}
