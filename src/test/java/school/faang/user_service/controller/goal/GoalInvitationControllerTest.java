package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.utils.goal.GoalInvitationPrepareData.getGoalInvitationDto;
import static school.faang.user_service.utils.goal.GoalInvitationPrepareData.getInviterIdFilter;

@ExtendWith(MockitoExtension.class)
class GoalInvitationControllerTest {
    private static final long GOAL_INVITATION_ID = 1L;

    @Mock
    private GoalInvitationService service;

    @InjectMocks
    private GoalInvitationController controller;

    @Test
    void testCreateInvitationSuccessTest() {
        doNothing().when(service).createInvitation(eq(getGoalInvitationDto()));

        controller.createInvitation(getGoalInvitationDto());

        verify(service).createInvitation(eq(getGoalInvitationDto()));
    }

    @Test
    void testAcceptInvitationSuccessTest() {
        doNothing().when(service).acceptGoalInvitation(GOAL_INVITATION_ID);

        controller.acceptGoalInvitation(GOAL_INVITATION_ID);

        verify(service).acceptGoalInvitation(eq(GOAL_INVITATION_ID));
    }

    @Test
    void testRejectInvitationSuccessTest() {
        doNothing().when(service).rejectGoalInvitation(GOAL_INVITATION_ID);

        controller.rejectGoalInvitation(GOAL_INVITATION_ID);

        verify(service).rejectGoalInvitation(eq(GOAL_INVITATION_ID));
    }

    @Test
    void testGetInvitationsWithFiltersSuccessTest() {
        when(service.getInvitationsWithFilters(any()))
                .thenReturn(List.of(getGoalInvitationDto()));

        List<GoalInvitationDto> invitations = controller.getInvitations(getInviterIdFilter());

        verify(service).getInvitationsWithFilters(any());
        assertEquals(1L, invitations.size());
    }
}