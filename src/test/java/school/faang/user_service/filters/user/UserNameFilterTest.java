package school.faang.user_service.filters.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.user.impl.UserNameFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserNameFilterTest {
    private final UserNameFilter filter = new UserNameFilter();
    private UserFilterDto filterDto;
    User user1 = User.builder().id(1L).username("Mary").build();
    User user2 = User.builder().id(2L).username("John").build();
    Stream<User> stream = Stream.of(user1, user2);

    @BeforeEach
    public void init() {
        filterDto = new UserFilterDto();
    }

    @Test
    public void testApplySuccessCase() {
        filterDto.setNamePattern("John");

        List<User> actual = filter.apply(stream, filterDto).toList();

        assertEquals(1, actual.size());
        assertEquals(user2, actual.get(0));
    }

    @Test
    public void testApplyCaseWithNotFullString() {
        filterDto.setNamePattern("Jo");

        List<User> actual = filter.apply(stream, filterDto).toList();

        assertEquals(1, actual.size());
        assertEquals(user2, actual.get(0));
    }

    @Test
    public void testApplyWithNamePatternNull() {
        List<User> actual = filter.apply(stream, filterDto).toList();

        assertEquals(2, actual.size());
    }

    @Test
    public void testApplyWithBlankString() {
        filterDto.setNamePattern("");

        List<User> actual = filter.apply(stream, filterDto).toList();

        assertEquals(2, actual.size());
    }
}
