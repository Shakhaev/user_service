package school.faang.user_service.service.goal.operations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

@Component
@RequiredArgsConstructor
public class StatusUpdater {

    private final GoalInvitationRepository goalInvitationRepository;

    public void updateStatus(GoalInvitation invitation, RequestStatus status) {
        invitation.setStatus(status);
        goalInvitationRepository.save(invitation);
    }
}