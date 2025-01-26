package school.faang.user_service.filters.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.user.impl.UserCountryFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserCountryFilterTest {
    private final UserCountryFilter filter = new UserCountryFilter();
    private UserFilterDto filterDto;

    private User user1;
    private User user2;

    Stream<User> stream;

    @BeforeEach
    public void init() {
        filterDto = new UserFilterDto();
        Country russia = Country.builder().title("russia").build();
        Country usa = Country.builder().title("usa").build();
        user1 = User.builder().country(russia).build();
        user2 = User.builder().country(usa).build();
        stream = Stream.of(user1, user2);
    }

    @Test
    public void testApplySuccessCase() {
        filterDto.setCountryPattern("russia");

        List<User> actual = filter.apply(stream, filterDto).toList();

        assertEquals(1, actual.size());
        assertEquals(user1, actual.get(0));
    }

    @Test
    public void testApplyCaseWithNotFullString() {
        filterDto.setCountryPattern("us");

        List<User> actual = filter.apply(stream, filterDto).toList();

        assertEquals(2, actual.size());
    }

    @Test
    public void testApplyWithCountryPatternNull() {
        List<User> actual = filter.apply(stream, filterDto).toList();

        assertEquals(2, actual.size());
    }

    @Test
    public void testApplyWithBlankString() {
        filterDto.setCountryPattern("");

        List<User> actual = filter.apply(stream, filterDto).toList();

        assertEquals(2, actual.size());
    }
}
