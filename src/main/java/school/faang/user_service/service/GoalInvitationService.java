package school.faang.user_service.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;

    public GoalInvitation createInvitation(@NonNull GoalInvitation invitation) {
        if (Objects.equals(invitation.getInviter(), invitation.getInvited()))
            throw new RuntimeException();

        if (!userRepository.existsById(invitation.getInvited().getId())) {
            throw new IllegalArgumentException("Invited user does not exist.");
        }

        if (!userRepository.existsById(invitation.getInviter().getId())) {
            throw new IllegalArgumentException("Inviting user does not exist.");
        }

        return goalInvitationRepository.save(invitation);
    }
}