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
    private static final int MAX_ACTIVE_GOALS = 3;

    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;

    public GoalInvitation createInvitation(GoalInvitation invitation) {
        Long inviterId = invitation.getInviter().getId();
        Long invitedId = invitation.getInvited().getId();

        if (Objects.equals(invitedId, inviterId)) {
            throw new IllegalArgumentException("The user doesn't create invitation by-self");
        }

        if (!userRepository.existsById(inviterId)) {
            throw new IllegalArgumentException("Inviter does not exist.");
        }

        if (!userRepository.existsById(invitedId)) {
            throw new IllegalArgumentException("Invited does not exist.");
        }

        return goalInvitationRepository.save(invitation);
    }
}