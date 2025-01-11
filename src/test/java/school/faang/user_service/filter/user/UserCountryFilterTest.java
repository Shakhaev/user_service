package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.Country;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserCountryFilterTest {

    private UserCountryFilter userCountryFilter;

    @BeforeEach
    void setUp() {
        userCountryFilter = new UserCountryFilter();
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenCountryPatternIsNotNull() {
        UserFilterDto filters = new UserFilterDto();
        filters.setCountryPattern("USA");

        boolean result = userCountryFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenCountryPatternIsNull() {
        UserFilterDto filters = new UserFilterDto();

        boolean result = userCountryFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldFilterUsersWithMatchingCountry() {
        UserFilterDto filters = new UserFilterDto();
        filters.setCountryPattern("USA");

        Country usa = new Country();
        usa.setTitle("USA");

        Country canada = new Country();
        canada.setTitle("Canada");

        User user1 = new User();
        user1.setCountry(usa);

        User user2 = new User();
        user2.setCountry(canada);

        User user3 = new User();
        user3.setCountry(usa);

        Stream<User> input = Stream.of(user1, user2, user3);
        Stream<User> expected = Stream.of(user1, user3);

        List<User> result = userCountryFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void apply_ShouldReturnEmptyStream_WhenNoUsersMatch() {
        UserFilterDto filters = new UserFilterDto();
        filters.setCountryPattern("Germany");

        Country usa = new Country();
        usa.setTitle("USA");

        Country canada = new Country();
        canada.setTitle("Canada");

        User user1 = new User();
        user1.setCountry(usa);

        User user2 = new User();
        user2.setCountry(canada);

        Stream<User> input = Stream.of(user1, user2);

        List<User> result = userCountryFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}