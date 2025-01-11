package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserExperienceMaxFilterTest {

    private UserExperienceMaxFilter userExperienceMaxFilter;

    @BeforeEach
    void setUp() {
        userExperienceMaxFilter = new UserExperienceMaxFilter();
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenExperienceMaxIsNotNull() {
        UserFilterDto filters = new UserFilterDto();
        filters.setExperienceMax(5);

        boolean result = userExperienceMaxFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenExperienceMaxIsNull() {
        UserFilterDto filters = new UserFilterDto();

        boolean result = userExperienceMaxFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldFilterUsersWithExperienceLessThanOrEqualToMax() {
        UserFilterDto filters = new UserFilterDto();
        filters.setExperienceMax(5);

        User user1 = new User();
        user1.setExperience(3);

        User user2 = new User();
        user2.setExperience(7);

        User user3 = new User();
        user3.setExperience(5);

        Stream<User> input = Stream.of(user1, user2, user3);
        Stream<User> expected = Stream.of(user1, user3);

        List<User> result = userExperienceMaxFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void apply_ShouldReturnEmptyStream_WhenNoUsersMatch() {
        UserFilterDto filters = new UserFilterDto();
        filters.setExperienceMax(2);

        User user1 = new User();
        user1.setExperience(3);

        User user2 = new User();
        user2.setExperience(7);

        Stream<User> input = Stream.of(user1, user2);

        List<User> result = userExperienceMaxFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}