package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationDtoOut;
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

import java.util.List;


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

    @Override
    public GoalInvitationDtoOut createInvitation(GoalInvitationDto goalInvitationDto) {

        invitationDtoValidator.validate(goalInvitationDto);
        log.info(String.format("Create invitation goalId: %s", goalInvitationDto.goalId()));

        GoalInvitation savedInvitation =
                goalInvitationRepository.save(goalInvitationMapper.toGoalInvitationEntity(goalInvitationDto));
        return goalInvitationMapper.toGoalInvitationDtoOut(savedInvitation);
    }

    @Override
    public GoalInvitationDtoOut acceptGoalInvitation(long id) {
        log.info("Accept goal invitation with id: {}.", id);
        GoalInvitation goalInvitation = findGoalInvitationById(id);

        User invitedUser = goalInvitation.getInvited();
        boolean isUserAlreadyWorkingOnGoal = containsGoalWithId(invitedUser.getGoals(), id);

        if (isUserAlreadyWorkingOnGoal) {
            throw new IllegalArgumentException(String.format("Exception invited user, " +
                    "the invited user is already working on this goal with id= %s", id));
        }

        if (invitedUser.getReceivedGoalInvitations().size() > MAX_ACTIVE_GOALS) {
            throw new IllegalArgumentException(String.format("Exception invited user can`t " +
                    "have more than %s goal invitations!", MAX_ACTIVE_GOALS));
        }

        invitedUser.getGoals().add(goalInvitation.getGoal());
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        userRepository.save(invitedUser);
        goalInvitationRepository.save(goalInvitation);

        return goalInvitationMapper.toGoalInvitationDtoOut(goalInvitation);
    }

    @Override
    public GoalInvitationDtoOut rejectGoalInvitation(long id) {
        log.info("Reject goal with id: {}.", id);

        GoalInvitation invitation = findGoalInvitationById(id);
        invitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(invitation);

        return goalInvitationMapper.toGoalInvitationDtoOut(invitation);
    }

    @Override
    public List<GoalInvitationDtoOut> getInvitations(InvitationFilterDto filterDto) {
        List<GoalInvitation> invitations = goalInvitationRepository.findAll();

        List<GoalInvitation> filteredInvitations = filters.stream()
                .filter(filter -> filter.isAcceptable(filterDto))
                .reduce(
                        List.of(),
                        (currentInvitations, filter) ->
                                filter.apply(currentInvitations.isEmpty() ? invitations : currentInvitations, filterDto),
                        (inv1, inv2) -> inv1
                );

        return filteredInvitations.stream().map(goalInvitationMapper::toGoalInvitationDtoOut).toList();
    }

    private boolean containsGoalWithId(List<Goal> goals, long goalId) {
        return goals.stream().anyMatch(goal -> goal.getId() == goalId);
    }

    private GoalInvitation findGoalInvitationById(long id) {
        return goalInvitationRepository.findById(id).orElseThrow(() ->
                new InvitationEntityNotFoundException(
                        String.format("Invitation to a goal with id: %s, not found!", id)));
    }
}
