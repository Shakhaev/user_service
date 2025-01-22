package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@RestController
@RequestMapping("/goal-invitations")
@RequiredArgsConstructor
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    @PostMapping
    public ResponseEntity<GoalInvitationDto> createInvitation(@RequestBody GoalInvitationDto goalInvitationDto) {
        GoalInvitationDto createdInvitation = goalInvitationService.createInvitation(goalInvitationDto);
        return ResponseEntity.ok(createdInvitation);
    }

    @PutMapping("/accept/{invitationId}")
    public ResponseEntity<GoalInvitationDto> acceptGoalInvitation(@PathVariable Long invitationId) {
        GoalInvitationDto acceptedInvitation = goalInvitationService.acceptGoalInvitation(invitationId);
        return ResponseEntity.ok(acceptedInvitation);
    }

    @PutMapping("/reject/{invitationId}")
    public ResponseEntity<GoalInvitationDto> rejectGoalInvitation(@PathVariable Long invitationId) {
        GoalInvitationDto rejectedInvitation = goalInvitationService.rejectGoalInvitation(invitationId);
        return ResponseEntity.ok(rejectedInvitation);
    }

    @GetMapping
    public ResponseEntity<List<GoalInvitationDto>> getInvitations(@RequestBody InvitationFilterDto filter) {
        List<GoalInvitationDto> invitations = goalInvitationService.getInvitations(filter);
        return ResponseEntity.ok(invitations);
    }
}