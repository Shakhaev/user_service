package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.Country;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserCountryFilterTest extends UserFilterTest {

    @BeforeEach
    void setUp() {
        userFilter = new UserCountryFilter();
        filters = new UserFilterDto();
    }

    @Test
    void isApplicableTest_ShouldReturnTrueWhenCountryPatternIsNotNull() {
        filters.setCountryPattern("USA");

        boolean result = userFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicableTest_ShouldReturnFalseWhenCountryPatternIsNull() {
        boolean result = userFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void applyTest_ShouldFilterUsersWithMatchingCountry() {
        filters.setCountryPattern("USA");

        Country usa = new Country();
        usa.setTitle("USA");

        Country canada = new Country();
        canada.setTitle("Canada");

        user1 = new User();
        user1.setCountry(usa);

        user2 = new User();
        user2.setCountry(canada);

        user3 = new User();
        user3.setCountry(usa);

        Stream<User> input = Stream.of(user1, user2, user3);
        Stream<User> expected = Stream.of(user1, user3);

        List<User> result = userFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void applyTest_ShouldReturnEmptyStreamWhenNoUsersMatch() {
        filters.setCountryPattern("Germany");

        Country usa = new Country();
        usa.setTitle("USA");

        Country canada = new Country();
        canada.setTitle("Canada");

        user1 = new User();
        user1.setCountry(usa);

        user2 = new User();
        user2.setCountry(canada);

        Stream<User> input = Stream.of(user1, user2);

        List<User> result = userFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}