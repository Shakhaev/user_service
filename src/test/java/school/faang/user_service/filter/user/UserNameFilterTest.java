package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserNameFilterTest {

    private UserNameFilter userNameFilter;

    @BeforeEach
    void setUp() {
        userNameFilter = new UserNameFilter();
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenNamePatternIsNotNull() {
        UserFilterDto filters = new UserFilterDto();
        filters.setNamePattern("JohnDoe");

        boolean result = userNameFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenNamePatternIsNull() {
        UserFilterDto filters = new UserFilterDto();

        boolean result = userNameFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldFilterUsersBasedOnNamePattern() {
        UserFilterDto filters = new UserFilterDto();
        filters.setNamePattern("JohnDoe");

        User user1 = new User();
        user1.setUsername("JohnDoe");

        User user2 = new User();
        user2.setUsername("JaneDoe");

        User user3 = new User();
        user3.setUsername("JohnDoe");

        Stream<User> input = Stream.of(user1, user2, user3);
        Stream<User> expected = Stream.of(user1, user3);

        List<User> result = userNameFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void apply_ShouldReturnEmptyStream_WhenNoUsersMatch() {
        UserFilterDto filters = new UserFilterDto();
        filters.setNamePattern("NotFound");

        User user1 = new User();
        user1.setUsername("JohnDoe");

        User user2 = new User();
        user2.setUsername("JaneDoe");

        Stream<User> input = Stream.of(user1, user2);

        List<User> result = userNameFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}