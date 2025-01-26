package school.faang.user_service.service.goal.filter.invitation;

import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class InvitationFilterIdInviterTest {
    public static final long INVITER_ID = 1L;
    @InjectMocks
    private InvitationFilterIdInviter invitationFilterIdInviter;

    private GoalInvitation goalInvitation;
    private InvitationFilterDto filters;
    private User user;

    @BeforeEach
    void setUp() {
        goalInvitation = new GoalInvitation();
        user = new User();
    }

    @Test
    void testIsAcceptableTrue() {
        filters = new InvitationFilterDto(null,null,INVITER_ID,null,null);
        assertTrue(invitationFilterIdInviter.isAcceptable(filters));
    }

    @Test
    void testIsAcceptableFalse() {
        filters = new InvitationFilterDto(null,null,null,null,null);
        assertFalse(invitationFilterIdInviter.isAcceptable(filters));
    }

    @Test
    void testApplyFilterWork() {
        fillParamsForApplayingFilter(1L, user);

        List<GoalInvitation> streamGoalInvitation = Collections.singletonList(goalInvitation);
        List<GoalInvitation> streamApplyFilter = invitationFilterIdInviter.apply(streamGoalInvitation, filters);
        assertEquals(1, streamApplyFilter.size());
    }

    @Test
    void testApplyFilterUnequal() {
        fillParamsForApplayingFilter(2L, user);

        List<GoalInvitation> streamGoalInvitation = Collections.singletonList(goalInvitation);
        List<GoalInvitation> streamApplyFilter = invitationFilterIdInviter.apply(streamGoalInvitation, filters);
        assertEquals(0, streamApplyFilter.size());
    }

    @Test
    void testApplyFilterInvitedIsNull() {
        filters = new InvitationFilterDto(null,null,INVITER_ID,null,null);
        goalInvitation.setInvited(null);

        List<GoalInvitation> streamGoalInvitation = Collections.singletonList(goalInvitation);
        List<GoalInvitation> streamApplyFilter = invitationFilterIdInviter.apply(streamGoalInvitation, filters);
        assertEquals(0, streamApplyFilter.size());
    }

    private void fillParamsForApplayingFilter(long userId, @NonNull User user) {
        filters = new InvitationFilterDto(null,null,INVITER_ID,null,null);

        user.setId(userId);
        goalInvitation.setInviter(user);
    }
}