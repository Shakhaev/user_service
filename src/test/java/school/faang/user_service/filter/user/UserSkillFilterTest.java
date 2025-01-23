package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserSkillFilterTest extends UserFilterTest {

    @BeforeEach
    void setUp() {
        userFilter = new UserSkillFilter();
        filters = new UserFilterDto();
    }

    @Test
    void isApplicableTest_ShouldReturnTrueWhenSkillPatternIsNotNull() {
        filters.setSkillPattern("Java");

        boolean result = userFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicableTest_ShouldReturnFalseWhenSkillPatternIsNull() {
        boolean result = userFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void applyTest_ShouldFilterUsersBasedOnSkillPattern() {
        filters.setSkillPattern("Java");

        Skill skill1 = new Skill();
        skill1.setTitle("Java");

        Skill skill2 = new Skill();
        skill2.setTitle("Python");

        user1 = new User();
        user1.setSkills(List.of(skill1));

        user2 = new User();
        user2.setSkills(List.of(skill2));

        user3 = new User();
        user3.setSkills(List.of(skill1));

        Stream<User> input = Stream.of(user1, user2, user3);
        Stream<User> expected = Stream.of(user1, user3);

        List<User> result = userFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void applyTest_ShouldReturnEmptyStreamWhenNoUsersMatch() {
        filters.setSkillPattern("Ruby");

        Skill skill1 = new Skill();
        skill1.setTitle("Java");

        Skill skill2 = new Skill();
        skill2.setTitle("Python");

        user1 = new User();
        user1.setSkills(List.of(skill1));

        user2 = new User();
        user2.setSkills(List.of(skill2));

        Stream<User> input = Stream.of(user1, user2);

        List<User> result = userFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}