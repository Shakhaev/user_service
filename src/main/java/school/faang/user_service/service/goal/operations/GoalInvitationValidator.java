package school.faang.user_service.service.goal.operations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.InvalidInvitationException;

@Component
@RequiredArgsConstructor
public class GoalInvitationValidator {

    public void validate(GoalInvitationDto dto, Goal goal) {
        if (goal == null) {
            throw new InvalidInvitationException("Goal cannot be null.");
        }
        if (dto.getInviterId().equals(dto.getInvitedUserId())) {
            throw new InvalidInvitationException("Inviter and invited user cannot be the same.");
        }
    }

    public void validate(GoalInvitation invitation) {
        if (invitation.getGoal() == null) {
            throw new InvalidInvitationException("Goal cannot be null.");
        }
    }
}