package school.faang.user_service.filter.goal;
/*
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.goal.data.InviterNameFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class InviterNameFilterTest extends InvitationFilterTest {

    @BeforeEach
    void setUp() {
        invitationFilter = new InviterNameFilter();
        filters = new InvitationFilterDto();
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenNameIsNotNull() {
        filters.setInviterName("Jane Smith");

        boolean result = invitationFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenNameIsNull() {
        boolean result = invitationFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldFilterInvitationsByMatchingName() {
        filters.setInviterName("Jane Smith");

        invitation1 = new GoalInvitation();
        invitation1.setInviterName("Jane Smith");

        invitation2 = new GoalInvitation();
        invitation2.setInviterName("John Smith");

        invitation3 = new GoalInvitation();
        invitation3.setInviterName("Jane Smith");

        Stream<GoalInvitation> input = Stream.of(invitation1, invitation2, invitation3);
        Stream<GoalInvitation> expected = Stream.of(invitation1, invitation3);

        List<GoalInvitation> result = invitationFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void apply_ShouldReturnEmptyStream_WhenNoInvitationsMatch() {
        filters.setInviterName("Jane Doe");

        invitation1 = new GoalInvitation();
        invitation1.setInviterName("Jane Smith");

        invitation2 = new GoalInvitation();
        invitation2.setInviterName("John Smith");

        Stream<GoalInvitation> input = Stream.of(invitation1, invitation2);

        List<GoalInvitation> result = invitationFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}

 */