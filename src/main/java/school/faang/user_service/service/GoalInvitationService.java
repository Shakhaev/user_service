package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalInvitationDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalInvitationMapper goalInvitationMapper;

    public void createInvitation(GoalInvitationDto goalInvitationDto) {
        long inviterId = goalInvitationDto.inviterId();
        long invitedId = goalInvitationDto.invitedUserId();
        long goalId = goalInvitationDto.goalId();

        if (inviterId == invitedId) {
            throw new IllegalArgumentException("The invited user is a inviting user!");
        }
        if (isUserInDatabase(invitedId) || isUserInDatabase(inviterId)) {
            throw new IllegalArgumentException("There is user that is not in database!");
        }

        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(goalInvitationDto);
        Goal inviterGoal = findGoalById(goalId);

        goalInvitation.setGoal(inviterGoal);
        goalInvitation.setCreatedAt(LocalDateTime.now());
        goalInvitation.setUpdatedAt(LocalDateTime.now());

        goalInvitationRepository.save(goalInvitation);
    }

    private Goal findGoalById(long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("There is no goal with id : " + goalId));
    }

    private boolean isUserInDatabase(long userId) {
        return userRepository.findById(userId).isEmpty();
    }
}
