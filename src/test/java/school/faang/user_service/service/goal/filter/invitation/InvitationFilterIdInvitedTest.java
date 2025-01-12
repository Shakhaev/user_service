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

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class InvitationFilterIdInvitedTest {

    public static final long INVITED_ID = 1L;
    @InjectMocks
    private InvitationFilterIdInvited invitationFilterIdInvited;

    private GoalInvitation goalInvitation;
    private InvitationFilterDto filters;
    private User user;

    @BeforeEach
    void setUp() {
        filters = new InvitationFilterDto();
        goalInvitation = new GoalInvitation();
        user = new User();
    }

    @Test
    void testIsAcceptableTrue() {
        filters.setInvitedId(INVITED_ID);
        assertTrue(invitationFilterIdInvited.isAcceptable(filters));
    }

    @Test
    void testIsAcceptableFalse() {
        assertFalse(invitationFilterIdInvited.isAcceptable(filters));
    }

    @Test
    void testApplyFilterWork() {
        fillParamsForApplayingFilter(1L, user);

        Stream<GoalInvitation> streamGoalInvitation = Stream.of(goalInvitation);
        Stream<GoalInvitation> streamApplyFilter = invitationFilterIdInvited.apply(streamGoalInvitation, filters);
        assertEquals(1, streamApplyFilter.toList().size());
    }

    @Test
    void testApplyFilterUnequal() {
        fillParamsForApplayingFilter(2L, user);

        Stream<GoalInvitation> streamGoalInvitation = Stream.of(goalInvitation);
        Stream<GoalInvitation> streamApplyFilter = invitationFilterIdInvited.apply(streamGoalInvitation, filters);
        assertEquals(0, streamApplyFilter.toList().size());
    }

    @Test
    void testApplyFilterInvitedIsNull() {
        filters.setInvitedId(INVITED_ID);
        goalInvitation.setInvited(null);

        Stream<GoalInvitation> streamGoalInvitation = Stream.of(goalInvitation);
        Stream<GoalInvitation> streamApplyFilter = invitationFilterIdInvited.apply(streamGoalInvitation, filters);
        assertEquals(0, streamApplyFilter.toList().size());
    }

    private void fillParamsForApplayingFilter(long userId, @NonNull User user) {
        filters.setInvitedId(INVITED_ID);
        user.setId(userId);
        goalInvitation.setInvited(user);
    }
}