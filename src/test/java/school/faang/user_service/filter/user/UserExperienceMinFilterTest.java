package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserExperienceMinFilterTest {

    private UserExperienceMinFilter userExperienceMinFilter;

    @BeforeEach
    void setUp() {
        userExperienceMinFilter = new UserExperienceMinFilter();
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenExperienceMinIsNotNull() {
        UserFilterDto filters = new UserFilterDto();
        filters.setExperienceMin(2);

        boolean result = userExperienceMinFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenExperienceMinIsNull() {
        UserFilterDto filters = new UserFilterDto();

        boolean result = userExperienceMinFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldFilterUsersWithExperienceGreaterThanOrEqualToMin() {
        UserFilterDto filters = new UserFilterDto();
        filters.setExperienceMin(5);

        User user1 = new User();
        user1.setExperience(3);

        User user2 = new User();
        user2.setExperience(7);

        User user3 = new User();
        user3.setExperience(5);

        Stream<User> input = Stream.of(user1, user2, user3);
        Stream<User> expected = Stream.of(user2, user3);

        List<User> result = userExperienceMinFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void apply_ShouldReturnEmptyStream_WhenNoUsersMatch() {
        UserFilterDto filters = new UserFilterDto();
        filters.setExperienceMin(8);

        User user1 = new User();
        user1.setExperience(3);

        User user2 = new User();
        user2.setExperience(7);

        Stream<User> input = Stream.of(user1, user2);

        List<User> result = userExperienceMinFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}