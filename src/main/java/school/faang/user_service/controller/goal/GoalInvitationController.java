package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;
    private final GoalInvitationMapper goalInvitationMapper;

    public GoalInvitationDto createInvitation(@RequestBody @Valid GoalInvitationDto dto) {
        GoalInvitation result = goalInvitationService.createInvitation(dto);
        return goalInvitationMapper.toDto(result);
    }

    public GoalInvitationDto acceptGoalInvitation(Long id) {
        GoalInvitation result = goalInvitationService.acceptGoalInvitation(id);
        return goalInvitationMapper.toDto(result);
    }

    public void rejectGoalInvitation(Long id) {
        goalInvitationService.rejectGoalInvitation(id);
    }

    public List<GoalInvitationDto> getInvitations(@RequestBody InvitationFilterDto filters) {
        List<GoalInvitation> filteredInvitations = goalInvitationService.getInvitations(filters);
        return goalInvitationMapper.toDtoList(filteredInvitations);
    }
}