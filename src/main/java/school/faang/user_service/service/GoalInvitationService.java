package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.filter.InvitationFilter;
import school.faang.user_service.dto.goal.filter.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private static final int MAX_GOALS = 3;

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper mapper;
    private final UserService userService;
    private final GoalService goalService;
    private final List<InvitationFilter> invitationFilters;

    public void createInvitation(GoalInvitationDto goalInvitationDto) {
        validateInvitationUsers(goalInvitationDto);
        goalInvitationRepository.save(mapper.toEntity(goalInvitationDto));
    }

    public void acceptGoalInvitation(long goalInvitationId) {
        GoalInvitation goalInvitation = findGoalInvitationById(goalInvitationId);
        long invitedUserId = goalInvitation.getInvited().getId();
        long goalId = goalInvitation.getGoal().getId();
        User invitedUser = getUserById(invitedUserId);
        validateUserAndGoal(invitedUser, goalId);
        acceptInvitation(goalInvitationId);
        addNewGoal(goalId, invitedUser);
    }

    public void rejectGoalInvitation(long goalInvitationId) {
        GoalInvitation goalInvitation = findGoalInvitationById(goalInvitationId);
        long goalId = goalInvitation.getGoal().getId();
        if (isExistsGoal(goalId)) {
            goalInvitation.setStatus(RequestStatus.REJECTED);
            goalInvitationRepository.save(goalInvitation);
        } else {
            throw new IllegalArgumentException("Goal with id = " + goalId + " doesn't exist");
        }
    }

    public List<GoalInvitationDto> getInvitationsWithFilters(InvitationFilterDto filters) {
        List<GoalInvitation> goalInvitations = goalInvitationRepository.findAll();
        for (InvitationFilter filter : invitationFilters) {
            if (filter.isApplicable(filters)) {
                goalInvitations = filter.apply(goalInvitations, filters);
            }
        }

        return goalInvitations.stream()
                .map(mapper::toDTO)
                .toList();
    }

    private void validateInvitationUsers(GoalInvitationDto dto) {
        long inviterId = dto.getInviterId();
        if (isNotExistsUser(inviterId)) {
            throw new IllegalArgumentException("Inviter user with id = " + inviterId + " doesn't exist");
        }
        long invitedUserId = dto.getInvitedUserId();
        if (isNotExistsUser(invitedUserId)) {
            throw new IllegalArgumentException("Invited user with id = " + invitedUserId + " doesn't exist");
        }
        if (!isUsersDifferent(inviterId, invitedUserId)) {
            throw new IllegalArgumentException("Inviter user and invited user not different");
        }
    }

    private boolean isNotExistsUser(long userId) {
        return !userService.existsById(userId);
    }

    private boolean isUsersDifferent(long inviterId, long invitedUserId) {
        return inviterId != invitedUserId;
    }


    private User getUserById(Long userId) {
        return userService.findById(userId);
    }

    private void validateUserAndGoal(User user, long goalId) {
        if (isGoalsMoreThanMax(user)) {
            throw new IllegalArgumentException("User goals is more than max");
        }
        if (!isExistsGoal(goalId)) {
            throw new IllegalArgumentException("Goal with id = " + goalId + " doesn't exists");
        }
        if (isGoalInWorkByUser(user, getGoalById(goalId))) {
            throw new IllegalArgumentException("Goal with id = "
                    + goalId + " already in work for user with id = " + user.getId());
        }
    }

    private boolean isGoalsMoreThanMax(User user) {
        int activeGoalsCount = user.getGoals().stream()
                .filter(goal -> goal.getStatus().equals(GoalStatus.ACTIVE))
                .toList()
                .size();
        return activeGoalsCount >= MAX_GOALS;
    }

    private boolean isExistsGoal(Long id) {
        return goalService.existsById(id);
    }

    private boolean isGoalInWorkByUser(User user, Goal currentGoal) {
        return user.getGoals().stream()
                .filter(f -> f.getStatus().equals(GoalStatus.ACTIVE))
                .anyMatch(g -> g.getId().equals(currentGoal.getId()));
    }

    private GoalInvitation findGoalInvitationById(Long goalId) {
        return goalInvitationRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Invitation with id = " + goalId + " doesn't exist"));
    }

    private void acceptInvitation(long invitationId) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(invitationId).orElseThrow(
                () -> new IllegalArgumentException("Invitation with id = " + invitationId + " doesn't exist"));
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        goalInvitationRepository.save(goalInvitation);
    }

    private void addNewGoal(long goalId, User user) {
        Goal newGoal = getGoalById(goalId);
        newGoal.getUsers().add(user);
        goalService.update(newGoal);
    }

    private Goal getGoalById(long id) {
        return goalService.findById(id);
    }
}
