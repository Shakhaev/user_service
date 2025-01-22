package school.faang.user_service.service.goal.operations;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class StatusUpdaterTest {

    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @InjectMocks
    private StatusUpdater statusUpdater;

    @Test
    void testUpdateStatus() {
        GoalInvitation invitation = new GoalInvitation();

        statusUpdater.updateStatus(invitation, RequestStatus.ACCEPTED);

        verify(goalInvitationRepository).save(invitation);
        assertEquals(RequestStatus.ACCEPTED, invitation.getStatus());
    }
}