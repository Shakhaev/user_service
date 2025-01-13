package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserExperienceMaxFilterTest extends UserFilterTest {

    @BeforeEach
    void setUp() {
        userFilter = new UserExperienceMaxFilter();
        filters = new UserFilterDto();
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenExperienceMaxIsNotNull() {
        filters.setExperienceMax(5);

        boolean result = userFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenExperienceMaxIsNull() {
        boolean result = userFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldFilterUsersWithExperienceLessThanOrEqualToMax() {
        filters.setExperienceMax(5);

        user1 = new User();
        user1.setExperience(3);

        user2 = new User();
        user2.setExperience(7);

        user3 = new User();
        user3.setExperience(5);

        Stream<User> input = Stream.of(user1, user2, user3);
        Stream<User> expected = Stream.of(user1, user3);

        List<User> result = userFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void apply_ShouldReturnEmptyStream_WhenNoUsersMatch() {
        filters.setExperienceMax(2);

        user1 = new User();
        user1.setExperience(3);

        user2 = new User();
        user2.setExperience(7);

        Stream<User> input = Stream.of(user1, user2);

        List<User> result = userFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}