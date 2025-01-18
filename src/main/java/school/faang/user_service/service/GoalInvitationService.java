package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filters.goal.InvitationFilter;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private static final int GOALS_LIMIT = 3;

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalService goalService;
    private final UserService userService;
    private final List<InvitationFilter> invitationFilters;

    public GoalInvitation createInvitation(GoalInvitation goalInvitation) {
        Long goalId = goalInvitation.getGoal().getId();
        Long inviterId = goalInvitation.getInviter().getId();
        Long invitedId = goalInvitation.getInvited().getId();

        if (inviterId == null || invitedId == null) {
            throw new IllegalArgumentException("There is no inviter or invited user");
        }
        if (inviterId.equals(invitedId)) {
            throw new IllegalArgumentException("Inviter and invited user have the same id");
        }

        Goal goal = goalService.getGoalById(goalId);
        User inviter = userService.getUserById(inviterId);
        User invited = userService.getUserById(invitedId);

        goalInvitation.setGoal(goal);
        goalInvitation.setInviter(inviter);
        goalInvitation.setInvited(invited);
        goalInvitation.setStatus(RequestStatus.PENDING);

        GoalInvitation createdInvitation = goalInvitationRepository.save(goalInvitation);

        return createdInvitation;
    }

    public GoalInvitation acceptGoalInvitation(long goalInvitationId) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(goalInvitationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("There is no invitation with id: " + goalInvitationId));
        User user = goalInvitation.getInvited();
        List<Goal> userGoals = user.getGoals();
        Goal goal = goalInvitation.getGoal();
        List<User> goalUsers = goal.getUsers();

        if (userGoals.size() >= GOALS_LIMIT) {
            throw new IllegalArgumentException("User already has limit of goals. Limit is " + GOALS_LIMIT);
        }
        if (goalUsers.contains(user)) {
            throw new IllegalArgumentException("User with id = " + user.getId() + " is already working on goal with id = " + goal.getId());
        }

        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        user.getGoals().add(goal);
        goal.getUsers().add(user);

        userService.updateUser(user);
        goalService.updateGoal(goal);
        GoalInvitation updated = goalInvitationRepository.save(goalInvitation);

        return updated;
    }

    public GoalInvitation rejectGoalInvitation(long goalInvitationId) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(goalInvitationId)
                .orElseThrow(() -> new IllegalArgumentException("There is no invitation with id = " + goalInvitationId));

        goalInvitation.setStatus(RequestStatus.REJECTED);

        GoalInvitation updated = goalInvitationRepository.save(goalInvitation);

        return updated;
    }

    public List<GoalInvitation> getInvitations(InvitationFilterDto invitationFilter) {
        Stream<GoalInvitation> invitations = goalInvitationRepository.findAll().stream();

        List<InvitationFilter> applicable =  invitationFilters.stream()
                .filter((filter) -> filter.isApplicable(invitationFilter))
                .toList();

        for (InvitationFilter filter : applicable) {
            invitations = filter.apply(invitations, invitationFilter);
        }

        return invitations.toList();
    }

}
