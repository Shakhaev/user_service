package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.service.GoalInvitationService;
import school.faang.user_service.validator.goal.GoalInvitationDtoValidator;

@RequiredArgsConstructor
@Controller
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;
    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalInvitationDtoValidator validator;

    public GoalInvitationDto createInvitation(GoalInvitationDto dto) {
        validator.validateDto(dto);
        GoalInvitation entity = goalInvitationMapper.toEntity(dto);
        GoalInvitation result = goalInvitationService.createInvitation(entity);
        return goalInvitationMapper.toDto(result);
    }

    public GoalInvitationDto acceptGoalInvitation(Long id) {
        GoalInvitation result = goalInvitationService.acceptGoalInvitation(id);
        return goalInvitationMapper.toDto(result);
    }

    public void rejectGoalInvitation(Long id) {
        goalInvitationService.rejectGoalInvitation(id);
    }

//    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter) {
//
//    }
}
