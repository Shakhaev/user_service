package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PremiumFilterTest {
    private PremiumFilter premiumFilter;
    private UserFilterDto filter;

    @BeforeEach
    public void setUp() {
        premiumFilter = new PremiumFilter();
        filter = new UserFilterDto();
    }

    @Test
    void testIsApplicableWithNull() {
        filter.setPremium(null);

        assertFalse(premiumFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicableWithValid() {
        filter.setPremium(true);

        assertTrue(premiumFilter.isApplicable(filter));
    }

    @Test
    void testApplyWithNonMatching() {
        User user1 = new User();
        User user2 = new User();
        Stream<User> users = Stream.of(user1, user2);
        UserFilterDto filter = new UserFilterDto();
        filter.setPremium(true);

        Stream<User> filteredUsers = premiumFilter.apply(users, filter);

        List<User> result = filteredUsers.toList();
        assertTrue(result.isEmpty());
    }

    @Test
    void testApplyWithMatching() {
        Premium premium = Premium.builder()
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .build();
        User user1 = new User();
        user1.setPremium(premium);
        User user2 = new User();
        Stream<User> users = Stream.of(user1, user2);
        filter.setPremium(true);

        Stream<User> filteredUsers = premiumFilter.apply(users, filter);

        List<User> result = filteredUsers.toList();
        assertEquals(1, result.size());
        assertTrue(result.contains(user1));
    }

}