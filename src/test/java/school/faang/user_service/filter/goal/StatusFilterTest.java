package school.faang.user_service.filter.goal;
/*
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.goal.data.StatusFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StatusFilterTest extends InvitationFilterTest {

    @BeforeEach
    void setUp() {
        invitationFilter = new StatusFilter();
        filters = new InvitationFilterDto();
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenStatusIsNotNull() {
        filters.setStatus("Pending");

        boolean result = invitationFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenStatusIsNull() {
        boolean result = invitationFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldFilterInvitationsByMatchingStatus() {
        filters.setStatus("Pending");

        invitation1 = new GoalInvitation();
        invitation1.setStatus("Pending");

        invitation2 = new GoalInvitation();
        invitation2.setStatus("Approved");

        invitation3 = new GoalInvitation();
        invitation3.setStatus("Pending");

        Stream<GoalInvitation> input = Stream.of(invitation1, invitation2, invitation3);
        Stream<GoalInvitation> expected = Stream.of(invitation1, invitation3);

        List<GoalInvitation> result = invitationFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void apply_ShouldReturnEmptyStream_WhenNoInvitationsMatch() {
        filters.setStatus("Rejected");

        invitation1 = new GoalInvitation();
        invitation1.setStatus("Pending");

        invitation2 = new GoalInvitation();
        invitation2.setStatus("Approved");

        Stream<GoalInvitation> input = Stream.of(invitation1, invitation2);

        List<GoalInvitation> result = invitationFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}
 */