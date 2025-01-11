package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserAboutFilterTest {

    private UserAboutFilter userAboutFilter;

    @BeforeEach
    void setUp() {
        userAboutFilter = new UserAboutFilter();
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenAboutPatternIsNotNull() {
        UserFilterDto filters = new UserFilterDto();
        filters.setAboutPattern("Software Developer");

        boolean result = userAboutFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenAboutPatternIsNull() {
        UserFilterDto filters = new UserFilterDto();

        boolean result = userAboutFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldFilterUsersWithMatchingAboutPattern() {
        UserFilterDto filters = new UserFilterDto();
        filters.setAboutPattern("Software Developer");

        User user1 = new User();
        user1.setAboutMe("Software Developer");

        User user2 = new User();
        user2.setAboutMe("Data Scientist");

        User user3 = new User();
        user3.setAboutMe("Software Developer");

        Stream<User> input = Stream.of(user1, user2, user3);
        Stream<User> expected = Stream.of(user1, user3);

        List<User> result = userAboutFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void apply_ShouldReturnEmptyStream_WhenNoUsersMatch() {
        UserFilterDto filters = new UserFilterDto();
        filters.setAboutPattern("Project Manager");

        User user1 = new User();
        user1.setAboutMe("Software Developer");

        User user2 = new User();
        user2.setAboutMe("Data Scientist");

        Stream<User> input = Stream.of(user1, user2);

        List<User> result = userAboutFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}