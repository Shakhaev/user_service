package school.faang.user_service.filter.goal;
/*
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.goal.data.InviterIdFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class InviterIdFilterTest extends InvitationFilterTest {

    @BeforeEach
    void setUp() {
        invitationFilter = new InviterIdFilter();
        filters = new InvitationFilterDto();
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenIdIsNotNull() {
        filters.setInviterId(1L);

        boolean result = invitationFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenIdIsNull() {
        boolean result = invitationFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldFilterInvitationsByMatchingId() {
        filters.setInviterId(1L);

        invitation1 = new GoalInvitation();
        invitation1.setInviterId(1L);

        invitation2 = new GoalInvitation();
        invitation2.setInviterId(2L);

        invitation3 = new GoalInvitation();
        invitation3.setInviterId(1L);

        Stream<GoalInvitation> input = Stream.of(invitation1, invitation2, invitation3);
        Stream<GoalInvitation> expected = Stream.of(invitation1, invitation3);

        List<GoalInvitation> result = invitationFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void apply_ShouldReturnEmptyStream_WhenNoInvitationsMatch() {
        filters.setInviterId(3L);

        invitation1 = new GoalInvitation();
        invitation1.setInviterId(1L);

        invitation2 = new GoalInvitation();
        invitation2.setInviterId(2L);

        Stream<GoalInvitation> input = Stream.of(invitation1, invitation2);

        List<GoalInvitation> result = invitationFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}

 */