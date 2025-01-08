package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.service.GoalInvitationService;

@RequiredArgsConstructor
@Controller
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;
    private final GoalInvitationMapper goalInvitationMapper;

    public void createInvitation(GoalInvitationDto invitationDto) {

        goalInvitationService.createInvitation(goalInvitationMapper.toEntity(invitationDto));

    }
}
