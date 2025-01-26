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
class InvitationFilterNameInvitedTest {
    public static final String INVITED_NAME_PATTERN = "Bo";
    @InjectMocks
    private InvitationFilterNameInvited invitationFilterNameInvited;

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
        filters = new InvitationFilterDto(null,INVITED_NAME_PATTERN, null,null,null);
        assertTrue(invitationFilterNameInvited.isAcceptable(filters));
    }

    @Test
    void testIsAcceptableFalse() {
        filters = new InvitationFilterDto(null,null, null,null,null);
        assertFalse(invitationFilterNameInvited.isAcceptable(filters));
    }

    @Test
    void testApplyFilterWork() {
        fillParamsForApplayingFilter("Bob", user);

        List<GoalInvitation> streamGoalInvitation = Collections.singletonList(goalInvitation);
        List<GoalInvitation> streamApplyFilter = invitationFilterNameInvited.apply(streamGoalInvitation, filters);
        assertEquals(1, streamApplyFilter.size());
    }

    @Test
    void testApplyFilterUnequal() {
        fillParamsForApplayingFilter("Tom", user);

        List<GoalInvitation> streamGoalInvitation = Collections.singletonList(goalInvitation);
        List<GoalInvitation> streamApplyFilter = invitationFilterNameInvited.apply(streamGoalInvitation, filters);
        assertEquals(0, streamApplyFilter.size());
    }

    @Test
    void testApplyFilterInvitedIsNull() {
        filters = new InvitationFilterDto(null,INVITED_NAME_PATTERN, null,null,null);
        goalInvitation.setInvited(null);

        List<GoalInvitation> streamGoalInvitation = Collections.singletonList(goalInvitation);
        List<GoalInvitation> streamApplyFilter = invitationFilterNameInvited.apply(streamGoalInvitation, filters);
        assertEquals(0, streamApplyFilter.size());
    }

    private void fillParamsForApplayingFilter(String name, @NonNull User user) {
        filters = new InvitationFilterDto(null,INVITED_NAME_PATTERN, null,null,null);
        user.setUsername(name);
        goalInvitation.setInvited(user);
    }
}