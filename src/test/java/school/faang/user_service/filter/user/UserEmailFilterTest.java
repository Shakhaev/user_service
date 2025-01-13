package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserEmailFilterTest extends UserFilterTest {

    @BeforeEach
    void setUp() {
        userFilter = new UserEmailFilter();
        filters = new UserFilterDto();
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenEmailPatternIsNotNull() {
        filters.setEmailPattern("user@example.com");

        boolean result = userFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenEmailPatternIsNull() {
        boolean result = userFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldFilterUsersWithMatchingEmail() {
        filters.setEmailPattern("user@example.com");

        user1 = new User();
        user1.setEmail("user@example.com");

        user2 = new User();
        user2.setEmail("another@example.com");

        user3 = new User();
        user3.setEmail("user@example.com");

        Stream<User> input = Stream.of(user1, user2, user3);
        Stream<User> expected = Stream.of(user1, user3);

        List<User> result = userFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void apply_ShouldReturnEmptyStream_WhenNoUsersMatch() {
        filters.setEmailPattern("nonexistent@example.com");

        user1 = new User();
        user1.setEmail("user@example.com");

        user2 = new User();
        user2.setEmail("another@example.com");

        Stream<User> input = Stream.of(user1, user2);

        List<User> result = userFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}