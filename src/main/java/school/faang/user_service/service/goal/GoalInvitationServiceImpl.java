package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


@Slf4j
@Service
@RequiredArgsConstructor
public class GoalInvitationServiceImpl implements GoalInvitationService {

    private static final int MAX_ACTIVE_GOALS = 3;

    private final GoalInvitationRepository goalInvitationRepository;
    private final InvitationDtoValidator invitationDtoValidator;
    private final GoalInvitationMapper goalInvitationMapper;
    private final UserRepository userRepository;
    private final List<InvitationFilter> filters;

    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto) {
        log.info("Create invitation.");

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
            throw new IllegalArgumentException(String.format("Exception invited user, " +
                    "the invited user is already working on this goal with id= %s", id));
        }

        if (invited.getReceivedGoalInvitations().size() > MAX_ACTIVE_GOALS) {
            throw new IllegalArgumentException(String.format("Exception invited user can`t " +
                    "have more than %s goal invitations!", MAX_ACTIVE_GOALS));
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

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filterDto) {
        log.info("Invitation filter.");

        List<GoalInvitation> invitations = goalInvitationRepository.findAll();

        Stream<GoalInvitation> invitationsAfterFilter = filters.stream()
                .filter(f -> f.isAcceptable(filterDto))
                .flatMap(f -> f.apply(invitations.stream(), filterDto));

        return invitationsAfterFilter.map(goalInvitationMapper::toDto).toList();
    }

    private GoalInvitation findGoalInvitationById(long id) {
        log.info("Find invitation with id: {}.", id);
        return goalInvitationRepository.findById(id).orElseThrow(() ->
                new InvitationEntityNotFoundException(
                        String.format("Invitation to a goal with id: %s, not found!", id)));
    }
}
