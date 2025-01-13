package school.faang.user_service.filters.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.user.impl.UserExperienceMinFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserExperienceMinTest {
    private final UserExperienceMinFilter filter = new UserExperienceMinFilter();
    private UserFilterDto filterDto;
    User user1 = User.builder().id(1L).experience(10).build();
    User user2 = User.builder().id(2L).experience(8).build();
    Stream<User> stream = Stream.of(user1, user2);

    @BeforeEach
    public void init() {
        filterDto = new UserFilterDto();
    }

    @Test
    public void testApplySuccessCase() {
        filterDto.setExperienceMin(9);

        List<User> actual = filter.apply(stream, filterDto).toList();

        assertEquals(1, actual.size());
        assertEquals(user1, actual.get(0));
    }

    @Test
    public void testApplyWithExperienceMinPatternNull() {
        List<User> actual = filter.apply(stream, filterDto).toList();

        assertEquals(2, actual.size());
    }

    @Test
    public void testApplyWithExperienceEqualMinExperience() {
        filterDto.setExperienceMin(8);

        List<User> actual = filter.apply(stream, filterDto).toList();

        assertEquals(2, actual.size());
    }
}
