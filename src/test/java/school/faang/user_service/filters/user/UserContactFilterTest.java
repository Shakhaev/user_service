package school.faang.user_service.filters.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.filters.user.impl.UserContactFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserContactFilterTest {
    private final UserContactFilter filter = new UserContactFilter();
    private UserFilterDto filterDto;

    private User user1;
    private User user2;

    Stream<User> stream;

    @BeforeEach
    public void init() {
        filterDto = new UserFilterDto();
        Contact johnny = Contact.builder().contact("Johnny").build();
        Contact jon = Contact.builder().contact("Jon").build();
        user1 = User.builder().contacts(List.of(johnny)).build();
        user2 = User.builder().contacts(List.of(jon)).build();
        stream = Stream.of(user1, user2);
    }

    @Test
    public void testApplySuccessCase() {
        filterDto.setContactPattern("Jon");

        List<User> actual = filter.apply(stream, filterDto).toList();

        assertEquals(1, actual.size());
        assertEquals(user2, actual.get(0));
    }

    @Test
    public void testApplyCaseWithNotFullString() {
        filterDto.setContactPattern("Jo");

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
        filterDto.setContactPattern("");

        List<User> actual = filter.apply(stream, filterDto).toList();

        assertEquals(2, actual.size());
    }
}
