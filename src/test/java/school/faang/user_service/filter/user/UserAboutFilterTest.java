package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserAboutFilterTest extends UserFilterTest {

    @BeforeEach
    void setUp() {
        userFilter = new UserAboutFilter();
        filters = new UserFilterDto();
    }

    @Test
    void isApplicableTest_ShouldReturnTrueWhenAboutPatternIsNotNull() {
        filters.setAboutPattern("Software Developer");

        boolean result = userFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicableTest_ShouldReturnFalseWhenAboutPatternIsNull() {
        boolean result = userFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void applyTest_ShouldFilterUsersWithMatchingAboutPattern() {
        filters.setAboutPattern("Software Developer");

        user1 = new User();
        user1.setAboutMe("Software Developer");

        user2 = new User();
        user2.setAboutMe("Data Scientist");

        user3 = new User();
        user3.setAboutMe("Software Developer");

        Stream<User> input = Stream.of(user1, user2, user3);
        Stream<User> expected = Stream.of(user1, user3);

        List<User> result = userFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void applyTest_ShouldReturnEmptyStreamWhenNoUsersMatch() {
        filters.setAboutPattern("Project Manager");

        user1 = new User();
        user1.setAboutMe("Software Developer");

        user2 = new User();
        user2.setAboutMe("Data Scientist");

        Stream<User> input = Stream.of(user1, user2);

        List<User> result = userFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}