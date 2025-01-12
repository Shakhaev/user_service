package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.filter.GoalInvitationFilter;
import school.faang.user_service.validation.GoalInvitationValidator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalInvitationValidator goalInvitationValidator;
    private final GoalInvitationMapper mapper;
    private final List<GoalInvitationFilter> filters;

    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto){
        goalInvitationValidator.validate(goalInvitationDto);
        GoalInvitation goalInvitation = mapper.toEntity(goalInvitationDto);
        return mapper.toDto(goalInvitationRepository.save(goalInvitation));
    }

    public void acceptInvitation(Long id){
        GoalInvitation goalInvitation = findGoalInvitation(id);
        User invited = goalInvitationValidator.uncrowdedInvitedUser(goalInvitation);
        goalInvitationValidator.isGoalExist(goalInvitation.getGoal().getId());

        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        invited.getGoals().add(goalInvitation.getGoal());

        goalInvitationRepository.save(goalInvitation);
        userRepository.save(invited);
    }

    public void rejectGoalInvitation(Long id){
        GoalInvitation goalInvitation = findGoalInvitation(id);

        goalInvitationValidator.isGoalExist(goalInvitation.getGoal().getId());
        goalInvitation.setStatus(RequestStatus.REJECTED);

        goalInvitationRepository.save(goalInvitation);
    }

    public List<GoalInvitationDto> getInvitations(GoalInvitationFilterDto filterDto){
        Stream<GoalInvitation> invitation = goalInvitationRepository.findAll().stream();

        return filters.stream()
                .filter(f -> f.applicable(filterDto))
                .flatMap( f -> f.apply(invitation, filterDto))
                .map(i -> mapper.toDto(i))
                .collect(Collectors.toList());
    }

    private GoalInvitation findGoalInvitation(Long id){
        return goalInvitationRepository.findById(id)
                .orElseThrow(() -> new DataValidationException( "Invitation not found"));
    }
}
