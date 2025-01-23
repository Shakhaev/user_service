package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserExperienceMinFilterTest extends UserFilterTest {

    @BeforeEach
    void setUp() {
        userFilter = new UserExperienceMinFilter();
        filters = new UserFilterDto();
    }

    @Test
    void isApplicableTest_ShouldReturnTrueWhenExperienceMinIsNotNull() {
        filters.setExperienceMin(2);

        boolean result = userFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicableTest_ShouldReturnFalseWhenExperienceMinIsNull() {
        boolean result = userFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void applyTest_ShouldFilterUsersWithExperienceGreaterThanOrEqualToMin() {
        filters.setExperienceMin(5);

        user1 = new User();
        user1.setExperience(3);

        user2 = new User();
        user2.setExperience(7);

        user3 = new User();
        user3.setExperience(5);

        Stream<User> input = Stream.of(user1, user2, user3);
        Stream<User> expected = Stream.of(user2, user3);

        List<User> result = userFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void applyTest_ShouldReturnEmptyStreamWhenNoUsersMatch() {
        filters.setExperienceMin(8);

        user1 = new User();
        user1.setExperience(3);

        user2 = new User();
        user2.setExperience(7);

        Stream<User> input = Stream.of(user1, user2);

        List<User> result = userFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}