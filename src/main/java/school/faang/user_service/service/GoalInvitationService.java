package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.GoalInvitationException;
import school.faang.user_service.filter.goal.InvitationFilter;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoalInvitationService {
    private static final int MAX_ACTIVE_GOALS = 3;

    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final List<InvitationFilter> invitationFilters;

    public GoalInvitation createInvitation(GoalInvitation invitation) {
        Long whoInviterId = invitation.getInviter().getId();
        Long invitedId = invitation.getInvited().getId();

        if (Objects.equals(invitedId, whoInviterId)) {
            throw new GoalInvitationException("The user doesn't create invitation by-self");
        }

        if (!isUserExists(whoInviterId) || !isUserExists(invitedId)) {
            throw new GoalInvitationException("User not found");
        }
        var newInvitation = goalInvitationRepository.save(invitation);
        log.info("New invitation to goal {} was created", newInvitation.getGoal().getTitle());
        return newInvitation;
    }

    public GoalInvitation acceptGoalInvitation(Long id) {
        GoalInvitation invitation = isGoalInvitationExists(id);

        User invitedUser = invitation.getInvited();
        List<GoalInvitation> invitedUserGoals = invitedUser.getReceivedGoalInvitations();

        if (invitedUserGoals.size() >= MAX_ACTIVE_GOALS) {
            throw new GoalInvitationException(String.format("User can not has more than %d active goals", MAX_ACTIVE_GOALS));
        }

        if (isGoalAlreadyContains(invitedUserGoals, id)) {
            throw new GoalInvitationException("User already has such goal");
        }
        invitation.setStatus(RequestStatus.ACCEPTED);
        invitedUser.getGoals().add(invitation.getGoal());
        userRepository.save(invitedUser);
        log.info("{} accepted invitation to goal {}", invitedUser.getUsername(), invitation.getGoal().getTitle());
        return goalInvitationRepository.save(invitation);
    }

    public void rejectGoalInvitation(Long id) {
        GoalInvitation invitation = isGoalInvitationExists(id);
        invitation.setStatus(RequestStatus.REJECTED);
        log.info("Invitation to goal {} rejected", invitation.getGoal().getTitle());

    }

    public List<GoalInvitation> getInvitations(InvitationFilterDto filters) {
        List<GoalInvitation> invitations = goalInvitationRepository.findAll();
        if (invitations.isEmpty()) {
            throw new NoSuchElementException("No one goal invitation created");
        }
        return invitationFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(invitations.stream(), filters))
                .toList();



    }

    private boolean isGoalAlreadyContains(List<GoalInvitation> invitedUserGoals, Long id) {
        return invitedUserGoals.stream()
                .map(GoalInvitation::getId)
                .anyMatch(idInv -> idInv.equals(id));
    }

    private boolean isUserExists(Long id) {
        return userRepository.existsById(id);
    }

    private GoalInvitation isGoalInvitationExists(Long id) {
        return goalInvitationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Goal invitation not found"));
    }
}