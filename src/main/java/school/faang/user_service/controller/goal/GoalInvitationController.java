package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.service.GoalInvitationService;

@RequiredArgsConstructor
@Controller
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;
    private final GoalInvitationMapper goalInvitationMapper;

    public GoalInvitationDto createInvitation(GoalInvitationDto dto) {
        GoalInvitation entity = goalInvitationMapper.toEntity(dto);
        GoalInvitation result = goalInvitationService.createInvitation(entity);
        return goalInvitationMapper.toDto(result);
    }

    public GoalInvitationDto acceptGoalInvitation(Long id) {
        GoalInvitation result = goalInvitationService.acceptGoalInvitation(id);
        return goalInvitationMapper.toDto(result);
    }


}
