package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserPageFilterTest {

    private UserPageFilter userPageFilter;

    @BeforeEach
    void setUp() {
        userPageFilter = new UserPageFilter();
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenPageAndPageSizeAreNotNull() {
        UserFilterDto filters = new UserFilterDto();
        filters.setPage(2);
        filters.setPageSize(3);

        boolean result = userPageFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenPageIsNull() {
        UserFilterDto filters = new UserFilterDto();
        filters.setPageSize(3);

        boolean result = userPageFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenPageSizeIsNull() {
        UserFilterDto filters = new UserFilterDto();
        filters.setPage(2);

        boolean result = userPageFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldReturnCorrectPageOfUsers() {
        UserFilterDto filters = new UserFilterDto();
        filters.setPage(2);
        filters.setPageSize(2);

        User user1 = new User();
        user1.setUsername("User1");

        User user2 = new User();
        user2.setUsername("User2");

        User user3 = new User();
        user3.setUsername("User3");

        User user4 = new User();
        user4.setUsername("User4");

        Stream<User> input = Stream.of(user1, user2, user3, user4);
        Stream<User> expected = Stream.of(user3, user4);

        List<User> result = userPageFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void apply_ShouldReturnEmptyStream_WhenPageOutOfBounds() {
        UserFilterDto filters = new UserFilterDto();
        filters.setPage(2);
        filters.setPageSize(2);

        User user1 = new User();
        user1.setUsername("User1");

        User user2 = new User();
        user2.setUsername("User2");

        Stream<User> input = Stream.of(user1, user2);

        List<User> result = userPageFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}