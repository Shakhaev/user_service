package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserEmailFilterTest {

    private UserEmailFilter userEmailFilter;

    @BeforeEach
    void setUp() {
        userEmailFilter = new UserEmailFilter();
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenEmailPatternIsNotNull() {
        UserFilterDto filters = new UserFilterDto();
        filters.setEmailPattern("user@example.com");

        boolean result = userEmailFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenEmailPatternIsNull() {
        UserFilterDto filters = new UserFilterDto();

        boolean result = userEmailFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldFilterUsersWithMatchingEmail() {
        UserFilterDto filters = new UserFilterDto();
        filters.setEmailPattern("user@example.com");

        User user1 = new User();
        user1.setEmail("user@example.com");

        User user2 = new User();
        user2.setEmail("another@example.com");

        User user3 = new User();
        user3.setEmail("user@example.com");

        Stream<User> input = Stream.of(user1, user2, user3);
        Stream<User> expected = Stream.of(user1, user3);

        List<User> result = userEmailFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void apply_ShouldReturnEmptyStream_WhenNoUsersMatch() {
        UserFilterDto filters = new UserFilterDto();
        filters.setEmailPattern("nonexistent@example.com");

        User user1 = new User();
        user1.setEmail("user@example.com");

        User user2 = new User();
        user2.setEmail("another@example.com");

        Stream<User> input = Stream.of(user1, user2);

        List<User> result = userEmailFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}