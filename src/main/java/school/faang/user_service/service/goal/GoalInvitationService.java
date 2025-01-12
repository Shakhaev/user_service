package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.InvitationEntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.goal.filter.invitation.InvitationFilter;
import school.faang.user_service.validator.goal.InvitationDtoValidator;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class GoalInvitationService {

    private static final int MAX_ACTIVE_GOALS = 3;

    private final GoalInvitationRepository goalInvitationRepository;
    private final InvitationDtoValidator invitationDtoValidator;
    private final GoalInvitationMapper goalInvitationMapper;
    private final UserRepository userRepository;
    private final List<InvitationFilter> filters;

    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto) {
        log.info("Сreate invitation.");

        invitationDtoValidator.validate(goalInvitationDto);
        GoalInvitation savedInvitation =
                goalInvitationRepository.save(goalInvitationMapper.toEntity(goalInvitationDto));
        return goalInvitationMapper.toDto(savedInvitation);
    }

    public GoalInvitationDto acceptGoalInvitation(long id) {
        log.info("Accept goal invitation with id: {}.", id);
        GoalInvitation goalInvitation = findGoalInvitationById(id);

        User invited = goalInvitation.getInvited();
        boolean isUserAlreadyWorkingAsGoal = containsGoalWithId(invited.getGoals(), id);

        if (isUserAlreadyWorkingAsGoal) {
            throw new IllegalArgumentException("Exception invited user," +
                    " the invited user is already working on this goal with id= " + id);
        }

        if (invited.getReceivedGoalInvitations().size() > MAX_ACTIVE_GOALS) {
            throw new IllegalArgumentException("Exception invited user can`t have more than "
                    + MAX_ACTIVE_GOALS + " goal invitations!");
        }

        invited.getGoals().add(goalInvitation.getGoal());
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        userRepository.save(invited);
        goalInvitationRepository.save(goalInvitation);

        return goalInvitationMapper.toDto(goalInvitation);
    }

    private boolean containsGoalWithId(List<Goal> goals, long goalsId) {
        return goals.stream().anyMatch(goal -> goal.getId() == goalsId);
    }

    public GoalInvitationDto rejectGoalInvitation(long id) {
        log.info("Reject goal with id: {}.", id);

        GoalInvitation invitation = findGoalInvitationById(id);
        invitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(invitation);
        return goalInvitationMapper.toDto(invitation);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter) {
        log.info("Invitation filter.");

        List<GoalInvitation> invitations = goalInvitationRepository.findAll();
        if (invitations.isEmpty()) {
            return new ArrayList<>();
        }
        filters.stream()
                .filter(f -> f.isAcceptable(filter))
                .forEach(f -> f.apply(invitations.stream(), filter));
        return invitations.stream().map(goalInvitationMapper::toDto).toList();
    }

    private GoalInvitation findGoalInvitationById(long id) {
        log.info("Find invitation with id: {}.", id);
        return goalInvitationRepository.findById(id).orElseThrow(() ->
                new InvitationEntityNotFoundException("Invitation to a goal with id: " + id + ", not found!"));
    }
}
