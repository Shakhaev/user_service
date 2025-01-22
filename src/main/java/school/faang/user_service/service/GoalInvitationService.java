package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.GoalInvitationException;
import school.faang.user_service.filter.goal.InvitationFilter;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoalInvitationService {
    private static final int MAX_ACTIVE_GOALS = 3;

    private final GoalInvitationRepository goalInvitationRepository;
    private final UserService userService;
    private final GoalService goalService;
    private final List<InvitationFilter> invitationFilters;

    @Transactional
    public GoalInvitation createInvitation(GoalInvitationDto dto) {
        Long inviterId = dto.getInviterId();
        Long invitedId = dto.getInvitedUserId();

        checkUsers(inviterId, invitedId);

        GoalInvitation invitation = GoalInvitation.builder()
                .inviter(userService.getUser(inviterId))
                .invited(userService.getUser(invitedId))
                .goal(goalService.getGoal(dto.getGoalId()))
                .status(RequestStatus.PENDING)
                .build();
        log.info("New invitation to goal {} was created", invitation.getGoal().getTitle());
        return goalInvitationRepository.save(invitation);
    }

    @Transactional
    public GoalInvitation acceptGoalInvitation(Long id) {
        GoalInvitation invitation = isGoalInvitationExists(id);

        User invitedUser = invitation.getInvited();
        List<GoalInvitation> invitedUserGoals = invitedUser.getReceivedGoalInvitations();

        if (invitedUserGoals.size() >= MAX_ACTIVE_GOALS) {
            throw new GoalInvitationException(String.format("User cannot has more than %d active goals", MAX_ACTIVE_GOALS));
        }

        if (invitedUser.getGoals().contains(invitation.getGoal())) {
            throw new GoalInvitationException("User already has such goal");
        }

        invitation.setStatus(RequestStatus.ACCEPTED);
        invitedUser.getGoals().add(invitation.getGoal());
        userService.saveUser(invitedUser);
        log.info("{} accepted invitation to goal {}", invitedUser.getUsername(), invitation.getGoal().getTitle());
        return goalInvitationRepository.save(invitation);
    }

    @Transactional
    public void rejectGoalInvitation(Long id) {
        GoalInvitation invitation = isGoalInvitationExists(id);
        invitation.setStatus(RequestStatus.REJECTED);
        log.info("Invitation to goal {} rejected", invitation.getGoal().getTitle());
    }

    @Transactional
    public List<GoalInvitation> getInvitations(InvitationFilterDto filters) {
        List<GoalInvitation> invitations = goalInvitationRepository.findAll();

        return invitationFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(invitations.stream(), filters))
                .toList();
    }

    private void checkUsers(Long inviterId, Long invitedId) {
        if (Objects.equals(invitedId, inviterId)) {
            throw new GoalInvitationException("User cannot create invitation for himself");
        }

        if (isUserNotExists(inviterId) || isUserNotExists(invitedId)) {
            throw new GoalInvitationException("Inviter or invited user not found");
        }
    }

    private boolean isUserNotExists(Long id) {
        return !userService.isUserExistById(id);
    }

    private GoalInvitation isGoalInvitationExists(Long id) {
        return goalInvitationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Goal invitation not found"));
    }
}