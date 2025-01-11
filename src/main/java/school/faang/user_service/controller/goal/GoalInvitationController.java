package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.GoalInvitationServiceImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/invitation-goals")
public class GoalInvitationController {
    private final GoalInvitationServiceImpl service;

    @PostMapping
    public void createInvitation(@RequestBody GoalInvitationDto invitation) {
        service.createInvitation(invitation);
    }

    @PutMapping("/accept/{id}")
    public void acceptGoalInvitation(@PathVariable("id") long id) {
        service.acceptGoalInvitation(id);
    }

    @PutMapping("/reject/{id}")
    public void rejectGoalInvitation(@PathVariable("id") long id) {
        service.rejectGoalInvitation(id);
    }

    @GetMapping
    public List<GoalInvitationDto> getInvitations(@RequestBody InvitationFilterDto dto) {
        return service.getInvitationsWithFilters(dto);
    }
}
