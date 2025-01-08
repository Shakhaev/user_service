package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

@RequiredArgsConstructor
@Service
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;

    public void createInvitation(GoalInvitation invitation) {
        goalInvitationRepository.save(invitation);
    }
}
