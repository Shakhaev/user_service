package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@RestController
@RequestMapping("/goal-invitation")
@RequiredArgsConstructor
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    @PostMapping("/create")
    public ResponseEntity<String> createInvitation(@RequestBody GoalInvitationDto goalInvitationDto) {
        goalInvitationService.createInvitation(goalInvitationDto);
        return ResponseEntity.ok("Invitation created successfully");
    }

    @PostMapping("/accept/{invitationId}")
    public ResponseEntity<String> acceptInvitation(@PathVariable Long invitationId) {
        goalInvitationService.acceptGoalInvitation(invitationId);
        return ResponseEntity.ok("Invitation accepted successfully");
    }

    @PostMapping("/reject/{invitationId}")
    public ResponseEntity<String> rejectInvitation(@PathVariable Long invitationId) {
        goalInvitationService.rejectGoalInvitation(invitationId);
        return ResponseEntity.ok("Invitation rejected successfully");
    }

    @GetMapping("/invitations")
    public List<GoalInvitationDto> getInvitations(@RequestBody InvitationFilterDto filter) {
        return goalInvitationService.getInvitations(filter);
    }
}