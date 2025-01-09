package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalInvitationDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;

    public void createInvitation(GoalInvitationDto goalInvitationDto) {
        Goal goal = new Goal();
    }
}
