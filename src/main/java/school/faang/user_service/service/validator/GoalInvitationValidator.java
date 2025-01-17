package school.faang.user_service.service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

@Component
@RequiredArgsConstructor
public class GoalInvitationValidator {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private static final int GOAL_MAX_NUMBER = 3;

    public void validate(GoalInvitationDto goalInvitationDto) {
        User inviter = userExist(goalInvitationDto.getInviterId());
        User invited = userExist(goalInvitationDto.getInvitedUserId());
        Long goalId = goalInvitationDto.getGoalId();

        if(invited.equals(inviter)){
            throw new DataValidationException("Inviter and Invited are the same person");
        }
        if(!isGoalExist(goalId)){
            throw new DataValidationException("Goal with ID: " + goalId + " does not exist");
        }
    }

    public User userExist(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User with ID: " + userId + " does not exist"));
    }

    public boolean isGoalExist(Long goalId){
        return goalRepository.existsById(goalId);
    }

    public User uncrowdedInvitedUser(GoalInvitation invitation){
        User invited = invitation.getInvited();
        if (invited.getGoals().size() == GOAL_MAX_NUMBER){
            throw new BusinessException("This user is full of work");
        }
        if(invited.getGoals().contains(invitation.getGoal())){
            throw new BusinessException("This user is already working on this goal");
        }
        return invited;
    }
}
