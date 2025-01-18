package school.faang.user_service.controller.goal;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RequestMapping("api/v1/goalInvitations")
@RestController
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;
    private final GoalInvitationMapper goalInvitationMapper;

    @PostMapping()
    public ResponseEntity<GoalInvitationDto> createInvitation(@RequestBody GoalInvitationDto invitation) {
        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(invitation);
        GoalInvitation createdInvitation = goalInvitationService.createInvitation(goalInvitation);
        return ResponseEntity.ok(goalInvitationMapper.toDto(createdInvitation));
    }

    @PutMapping("/{invitationId}")
    public ResponseEntity<GoalInvitationDto> acceptGoalInvitation(@PathVariable @Positive(message = "Invitation ID must be a positive number.") long invitationId) {
        GoalInvitation updatedGoalInvitation = goalInvitationService.acceptGoalInvitation(invitationId);
        return ResponseEntity.ok(goalInvitationMapper.toDto(updatedGoalInvitation));
    }

    @PutMapping("/{invitationId}")
    public ResponseEntity<GoalInvitationDto> rejectGoalInvitation(@PathVariable @Positive(message = "Invitation ID must be a positive number.") long invitationId) {
        GoalInvitation updatedgoalInvitation = goalInvitationService.rejectGoalInvitation(invitationId);
        return ResponseEntity.ok(goalInvitationMapper.toDto(updatedgoalInvitation));
    }

    @GetMapping()
    public ResponseEntity<List<GoalInvitationDto>> getInvitations(@RequestBody InvitationFilterDto filter) {
        List<GoalInvitation> filtered = goalInvitationService.getInvitations(filter);
        return ResponseEntity.ok(goalInvitationMapper.toDtoList(filtered));
    }
}

