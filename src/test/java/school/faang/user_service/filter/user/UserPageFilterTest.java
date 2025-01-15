package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserPageFilterTest extends UserFilterTest {

    @BeforeEach
    void setUp() {
        userFilter = new UserPageFilter();
        filters = new UserFilterDto();
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenPageAndPageSizeAreNotNull() {
        filters.setPage(2);
        filters.setPageSize(3);

        boolean result = userFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenPageIsNull() {
        filters.setPageSize(3);

        boolean result = userFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenPageSizeIsNull() {
        filters.setPage(2);

        boolean result = userFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldReturnCorrectPageOfUsers() {
        filters.setPage(2);
        filters.setPageSize(2);

        user1 = new User();
        user1.setUsername("User1");

        user2 = new User();
        user2.setUsername("User2");

        user3 = new User();
        user3.setUsername("User3");

        User user4 = new User();
        user4.setUsername("User4");

        Stream<User> input = Stream.of(user1, user2, user3, user4);
        Stream<User> expected = Stream.of(user3, user4);

        List<User> result = userFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void apply_ShouldReturnEmptyStream_WhenPageOutOfBounds() {
        filters.setPage(2);
        filters.setPageSize(2);

        user1 = new User();
        user1.setUsername("User1");

        user2 = new User();
        user2.setUsername("User2");

        Stream<User> input = Stream.of(user1, user2);

        List<User> result = userFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}