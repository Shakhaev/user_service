package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserSkillFilterTest {

    private UserSkillFilter userSkillFilter;

    @BeforeEach
    void setUp() {
        userSkillFilter = new UserSkillFilter();
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenSkillPatternIsNotNull() {
        UserFilterDto filters = new UserFilterDto();
        filters.setSkillPattern("Java");

        boolean result = userSkillFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenSkillPatternIsNull() {
        UserFilterDto filters = new UserFilterDto();

        boolean result = userSkillFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldFilterUsersBasedOnSkillPattern() {
        UserFilterDto filters = new UserFilterDto();
        filters.setSkillPattern("Java");

        Skill skill1 = new Skill();
        skill1.setTitle("Java");

        Skill skill2 = new Skill();
        skill2.setTitle("Python");

        User user1 = new User();
        user1.setSkills(List.of(skill1));

        User user2 = new User();
        user2.setSkills(List.of(skill2));

        User user3 = new User();
        user3.setSkills(List.of(skill1));

        Stream<User> input = Stream.of(user1, user2, user3);
        Stream<User> expected = Stream.of(user1, user3);

        List<User> result = userSkillFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void apply_ShouldReturnEmptyStream_WhenNoUsersMatch() {
        UserFilterDto filters = new UserFilterDto();
        filters.setSkillPattern("Ruby");

        Skill skill1 = new Skill();
        skill1.setTitle("Java");

        Skill skill2 = new Skill();
        skill2.setTitle("Python");

        User user1 = new User();
        user1.setSkills(List.of(skill1));

        User user2 = new User();
        user2.setSkills(List.of(skill2));

        Stream<User> input = Stream.of(user1, user2);

        List<User> result = userSkillFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}