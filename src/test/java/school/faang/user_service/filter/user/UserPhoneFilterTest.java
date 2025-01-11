package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserPhoneFilterTest {

    private UserPhoneFilter userPhoneFilter;

    @BeforeEach
    void setUp() {
        userPhoneFilter = new UserPhoneFilter();
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenPhonePatternIsNotNull() {
        UserFilterDto filters = new UserFilterDto();
        filters.setPhonePattern("1234567890");

        boolean result = userPhoneFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenPhonePatternIsNull() {
        UserFilterDto filters = new UserFilterDto();

        boolean result = userPhoneFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldFilterUsersBasedOnPhonePattern() {
        UserFilterDto filters = new UserFilterDto();
        filters.setPhonePattern("1234567890");

        User user1 = new User();
        user1.setPhone("1234567890");

        User user2 = new User();
        user2.setPhone("0987654321");

        User user3 = new User();
        user3.setPhone("1234567890");

        Stream<User> input = Stream.of(user1, user2, user3);
        Stream<User> expected = Stream.of(user1, user3);

        List<User> result = userPhoneFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void apply_ShouldReturnEmptyStream_WhenNoUsersMatch() {
        UserFilterDto filters = new UserFilterDto();
        filters.setPhonePattern("1112223333");

        User user1 = new User();
        user1.setPhone("1234567890");

        User user2 = new User();
        user2.setPhone("0987654321");

        Stream<User> input = Stream.of(user1, user2);

        List<User> result = userPhoneFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}