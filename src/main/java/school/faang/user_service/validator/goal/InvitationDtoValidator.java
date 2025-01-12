package school.faang.user_service.validator.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.NoSuchElementException;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvitationDtoValidator {
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    public void validate(final GoalInvitationDto goalInviteDto) {
        validateUserDoesNotInviteHimself(goalInviteDto);
        validateUserExists(goalInviteDto.getInviterId(), "Inviter");
        validateUserExists(goalInviteDto.getInvitedUserId(), "Invited");
        validateGoalExists(goalInviteDto.getGoalId());
    }

    private void validateUserDoesNotInviteHimself(GoalInvitationDto goalInviteDto) {
        log.info("Check that the user: {}, does not invite himself: {}", goalInviteDto.getInvitedUserId(),
                goalInviteDto.getInviterId());
        if (goalInviteDto.getInvitedUserId().equals(goalInviteDto.getInviterId())) {
            throw new DataValidationException("The user cannot invite himself!");
        }
    }

    private void validateUserExists(Long userId, String userType) {
        log.info("Checking existence of {} user, with id: {}", userType, userId);
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException(userType + " user with id: " + userId + " does not exist.");
        }
    }

    private void validateGoalExists(Long goalId) {
        if (!goalRepository.existsById(goalId)) {
            log.error("Goal with id: {} does not exist.", goalId);
            throw new NoSuchElementException("Goal with id: " + goalId + " does not exist.");
        }
    }
}
