package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserCityFilterTest extends UserFilterTest {

    @BeforeEach
    void setUp() {
        userFilter = new UserCityFilter();
        filters = new UserFilterDto();
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenCityPatternIsNotNull() {
        filters.setCityPattern("New York");

        boolean result = userFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenCityPatternIsNull() {
        boolean result = userFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldFilterUsersBasedOnCityPattern() {
        filters.setCityPattern("New York");

        user1 = new User();
        user1.setCity("New York");

        user2 = new User();
        user2.setCity("Los Angeles");

        user3 = new User();
        user3.setCity("New York");
        Stream<User> input = Stream.of(user1, user2, user3);
        Stream<User> expected = Stream.of(user1, user3);

        List<User> result = userFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void apply_ShouldReturnEmptyStream_WhenNoUsersMatch() {
        filters.setCityPattern("San Francisco");

        user1 = new User();
        user1.setCity("New York");

        user2 = new User();
        user2.setCity("Los Angeles");
        Stream<User> input = Stream.of(user1, user2);

        List<User> result = userFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}