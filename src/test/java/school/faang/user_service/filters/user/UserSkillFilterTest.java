package school.faang.user_service.filters.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.user.impl.UserSkillFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserSkillFilterTest {
    private final UserSkillFilter filter = new UserSkillFilter();
    private UserFilterDto filterDto;

    private User user1;
    private User user2;

    Stream<User> stream;

    @BeforeEach
    public void init() {
        filterDto = new UserFilterDto();
        Skill skill1 = Skill.builder().title("Programming").build();
        Skill skill2 = Skill.builder().title("Management").build();
        user1 = User.builder().skills(List.of(skill1)).build();
        user2 = User.builder().skills(List.of(skill2)).build();
        stream = Stream.of(user1, user2);
    }

    @Test
    public void testApplySuccessCase() {
        filterDto.setSkillPattern("Management");

        List<User> actual = filter.apply(stream, filterDto).toList();

        assertEquals(1, actual.size());
        assertEquals(user2, actual.get(0));
    }

    @Test
    public void testApplyCaseWithNotFullString() {
        filterDto.setSkillPattern("Pro");

        List<User> actual = filter.apply(stream, filterDto).toList();

        assertEquals(1, actual.size());
        assertEquals(user1, actual.get(0));
    }

    @Test
    public void testApplyWithSkillPatternNull() {
        List<User> actual = filter.apply(stream, filterDto).toList();

        assertEquals(2, actual.size());
    }

    @Test
    public void testApplyWithBlankString() {
        filterDto.setSkillPattern("");

        List<User> actual = filter.apply(stream, filterDto).toList();

        assertEquals(2, actual.size());
    }

}
