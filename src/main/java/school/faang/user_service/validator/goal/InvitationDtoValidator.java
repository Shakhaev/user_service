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
    public static final String INVITER = "Inviter";
    public static final String INVITED = "Invited";
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    public void validate(final GoalInvitationDto goalInviteDto) {
        validateUserDoesNotInviteHimself(goalInviteDto);
        validateUserExists(goalInviteDto.inviterId(), INVITER);
        validateUserExists(goalInviteDto.invitedUserId(), INVITED);
        validateGoalExists(goalInviteDto.goalId());
    }

    private void validateUserDoesNotInviteHimself(GoalInvitationDto goalInviteDto) {
        log.info("Check that the user: {}, does not invite himself: {}", goalInviteDto.invitedUserId(),
                goalInviteDto.inviterId());
        if (goalInviteDto.invitedUserId().equals(goalInviteDto.inviterId())) {
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
