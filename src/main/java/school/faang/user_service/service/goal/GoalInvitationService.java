package school.faang.user_service.service.goal;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.InvalidInvitationException;
import school.faang.user_service.filter.goal.data.InvitationFilter;
import school.faang.user_service.filter.goal.validation.GoalFilter;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final List<GoalFilter> goalFilters;

    private final List<InvitationFilter> invitationFilters;

    @Transactional
    public void createInvitation(GoalInvitationDto invitationDto) {
        GoalInvitation invitation = goalInvitationMapper.toEntity(invitationDto);

        Long inviterId = invitationDto.getInviterId();
        Long invitedUserId = invitationDto.getInvitedUserId();

        validateFilters(invitation.getGoal(), inviterId, invitedUserId);

        invitation.setStatus(RequestStatus.PENDING);
        goalInvitationRepository.save(invitation);
    }

    @Transactional
    public void acceptGoalInvitation(Long invitationId) {
        GoalInvitation invitation = goalInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new InvalidInvitationException("Invitation does not exist."));

        Long inviterId = invitation.getInviter().getId();
        Long invitedUserId = invitation.getInvited().getId();

        validateFilters(invitation.getGoal(), inviterId, invitedUserId);

        invitation.setStatus(RequestStatus.ACCEPTED);
        goalInvitationRepository.save(invitation);
    }

    @Transactional
    public void rejectGoalInvitation(Long invitationId) {
        GoalInvitation invitation = goalInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new InvalidInvitationException("Invitation does not exist."));

        validateFilters(invitation.getGoal(), invitation.getInviter().getId(), invitation.getInvited().getId());

        invitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(invitation);
    }

    @Transactional
    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter) {
        Stream<GoalInvitation> invitationsStream = goalInvitationRepository.findAll().stream();

        for (InvitationFilter invitationFilter : invitationFilters) {
            if (invitationFilter.isApplicable(filter)) {
                invitationsStream = invitationFilter.apply(invitationsStream, filter);
            }
        }

        return invitationsStream
                .map(goalInvitationMapper::toDto)
                .collect(Collectors.toList());
    }

    private void validateFilters(Goal goal, Long inviterId, Long invitedUserId) {
        goalFilters.forEach(filter -> filter.apply(goal, inviterId, invitedUserId));
    }
}