package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator {
    private static final long MONTHS_REPEAT_REQUEST_LIMIT = 3;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserService userService;

    public void validate(MentorshipRequestDto mentorshipRequestDto) {
        validateRequesterAndReceiverExists(mentorshipRequestDto);
        validateSelfRequest(mentorshipRequestDto);
        validateMonthLimitForRepeatRequest(mentorshipRequestDto);
    }

    public void validateRequesterHaveReceiverAsMentor(MentorshipRequest request) {
        User receiver = request.getReceiver();
        List<User> requesterMentors = request.getRequester().getMentors();

        if (requesterMentors.contains(receiver)) {
            String message = "Получатель запроса с id=" + receiver.getId() +
                    " уже является ментором пользователя c id=" + request.getRequester().getId();
            log.warn(message);
            throw new BusinessException(message);
        }
    }

    private void validateRequesterAndReceiverExists(MentorshipRequestDto mentorshipRequestDto) {
        userService.isUserExists(mentorshipRequestDto.getRequesterId());
        userService.isUserExists(mentorshipRequestDto.getReceiverId());
        log.info("Отправитель и получатель запроса на менторство существуют");
    }

    private void validateSelfRequest(MentorshipRequestDto mentorshipRequestDto) {
        if (Objects.equals(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId())) {
            throw new BusinessException("Нельзя отправить запрос на менторство самому себе");
        }
    }

    private void validateMonthLimitForRepeatRequest(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestRepository
                .findLatestRequest(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId())
                .filter(request -> !request.getCreatedAt().isBefore(getMonthLimitAgo()))
                .ifPresent(request -> {
                    throw new BusinessException("Запрос можно отправить раз в " +
                            MONTHS_REPEAT_REQUEST_LIMIT + " месяца");
                });
    }

    private LocalDateTime getMonthLimitAgo() {
        return LocalDateTime.now().minusMonths(MONTHS_REPEAT_REQUEST_LIMIT);
    }
}
