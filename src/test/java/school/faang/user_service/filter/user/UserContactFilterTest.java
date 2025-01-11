package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserContactFilterTest {

    private UserContactFilter userContactFilter;

    @BeforeEach
    void setUp() {
        userContactFilter = new UserContactFilter();
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenContactPatternIsNotNull() {
        UserFilterDto filters = new UserFilterDto();
        filters.setContactPattern("111");

        boolean result = userContactFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenContactPatternIsNull() {
        UserFilterDto filters = new UserFilterDto();

        boolean result = userContactFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldFilterUsersWithMatchingContact() {
        UserFilterDto filters = new UserFilterDto();
        filters.setContactPattern("111");

        User user1 = new User();
        Contact contact1 = new Contact();
        contact1.setContact("111");
        user1.setContacts(List.of(contact1));

        User user2 = new User();
        Contact contact2 = new Contact();
        contact2.setContact("222");
        user2.setContacts(List.of(contact2));

        User user3 = new User();
        Contact contact3 = new Contact();
        contact3.setContact("111");
        user3.setContacts(List.of(contact3));

        Stream<User> input = Stream.of(user1, user2, user3);
        Stream<User> expected = Stream.of(user1, user3);

        List<User> result = userContactFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void apply_ShouldReturnEmptyStream_WhenNoUsersMatch() {
        UserFilterDto filters = new UserFilterDto();
        filters.setContactPattern("000");

        User user1 = new User();
        Contact contact1 = new Contact();
        contact1.setContact("111");
        user1.setContacts(List.of(contact1));

        User user2 = new User();
        Contact contact2 = new Contact();
        contact2.setContact("222");
        user2.setContacts(List.of(contact2));

        Stream<User> input = Stream.of(user1, user2);

        List<User> result = userContactFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}