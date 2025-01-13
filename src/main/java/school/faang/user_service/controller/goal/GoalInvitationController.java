package school.faang.user_service.controller.goal;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationServiceImpl;

import java.util.List;

@RestController
@RequestMapping("api/goal")
@RequiredArgsConstructor
public class GoalInvitationController {

    private final GoalInvitationServiceImpl goalInvitationServiceImpl;

    @PostMapping("/invitation")
    public ResponseEntity<GoalInvitationDto> createInvitation(@RequestBody GoalInvitationDto invitation) {
        val invite = goalInvitationServiceImpl.createInvitation(invitation);
        return ResponseEntity.ok(invite);
    }

    @PutMapping("/accept/{id}")
    public ResponseEntity<GoalInvitationDto> acceptGoalInvitation(@PathVariable("id") @NotNull Long id) {
        val accept = goalInvitationServiceImpl.acceptGoalInvitation(id);
        return ResponseEntity.ok(accept);
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<GoalInvitationDto> rejectGoalInvitation(@PathVariable("id") @NotNull Long id) {
        val reject = goalInvitationServiceImpl.rejectGoalInvitation(id);
        return ResponseEntity.ok(reject);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<GoalInvitationDto>> getInvitations(@RequestBody InvitationFilterDto filter) {
        val invitationFilter = goalInvitationServiceImpl.getInvitations(filter);
        return ResponseEntity.ok(invitationFilter);
    }
}
