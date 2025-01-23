package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserPhoneFilterTest extends UserFilterTest {

    @BeforeEach
    void setUp() {
        userFilter = new UserPhoneFilter();
        filters = new UserFilterDto();
    }

    @Test
    void isApplicableTest_ShouldReturnTrueWhenPhonePatternIsNotNull() {
        filters.setPhonePattern("1234567890");

        boolean result = userFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicableTest_ShouldReturnFalseWhenPhonePatternIsNull() {
        boolean result = userFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void applyTest_ShouldFilterUsersBasedOnPhonePattern() {
        filters.setPhonePattern("1234567890");

        user1 = new User();
        user1.setPhone("1234567890");

        user2 = new User();
        user2.setPhone("0987654321");

        user3 = new User();
        user3.setPhone("1234567890");

        Stream<User> input = Stream.of(user1, user2, user3);
        Stream<User> expected = Stream.of(user1, user3);

        List<User> result = userFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void applyTest_ShouldReturnEmptyStreamWhenNoUsersMatch() {
        filters.setPhonePattern("1112223333");

        user1 = new User();
        user1.setPhone("1234567890");

        user2 = new User();
        user2.setPhone("0987654321");

        Stream<User> input = Stream.of(user1, user2);

        List<User> result = userFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}