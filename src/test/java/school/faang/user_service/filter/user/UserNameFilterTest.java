package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserNameFilterTest extends UserFilterTest {

    @BeforeEach
    void setUp() {
        userFilter = new UserNameFilter();
        filters = new UserFilterDto();
    }

    @Test
    void isApplicableTest_ShouldReturnTrueWhenNamePatternIsNotNull() {
        filters.setNamePattern("JohnDoe");

        boolean result = userFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicableTest_ShouldReturnFalseWhenNamePatternIsNull() {
        boolean result = userFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void applyTest_ShouldFilterUsersBasedOnNamePattern() {
        filters.setNamePattern("JohnDoe");

        user1 = new User();
        user1.setUsername("JohnDoe");

        user2 = new User();
        user2.setUsername("JaneDoe");

        user3 = new User();
        user3.setUsername("JohnDoe");

        Stream<User> input = Stream.of(user1, user2, user3);
        Stream<User> expected = Stream.of(user1, user3);

        List<User> result = userFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void applyTest_ShouldReturnEmptyStreamWhenNoUsersMatch() {
        filters.setNamePattern("NotFound");

        user1 = new User();
        user1.setUsername("JohnDoe");

        user2 = new User();
        user2.setUsername("JaneDoe");

        Stream<User> input = Stream.of(user1, user2);

        List<User> result = userFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}