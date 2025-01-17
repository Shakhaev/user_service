package school.faang.user_service.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.TestData;

import java.util.List;
import java.util.stream.Stream;

class SubscriptionUserPageFilterTest {
    private SubscriptionUserFilterDto subscriptionUserFilterDto;
    private final SubscriptionUserPageFilter filter = new SubscriptionUserPageFilter();
    private List<User> allUsers;

    @BeforeEach
    void setUp() {
        allUsers = TestData.getUsers();
    }

    @Test
    @DisplayName("Test true applicability user filter by Page/PageSize")
    void isApplicableTest() {
        boolean isApplicableExpected = false;
        subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .page(1)
                .pageSize(1)
                .build();
        boolean isApplicableActual = filter.isApplicable(subscriptionUserFilterDto);
        Assertions.assertEquals(isApplicableExpected, isApplicableActual);
    }

    @Test
    @DisplayName("Test result of user filter by Page/PageSize")
    void applyTrueSearch() {
        subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .page(1)
                .pageSize(5)
                .build();
        Stream<User> userStream = filter.apply(allUsers.stream(), subscriptionUserFilterDto);
        List<User> actualUsers = userStream.toList();

        Assertions.assertEquals(actualUsers, allUsers);
    }

    @Test
    @DisplayName("Test result of user filter by Page/PageSize. 2nd page")
    void applyTrueSearchOnSecondPage() {
        subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .page(2)
                .pageSize(1)
                .build();
        Stream<User> userStream = filter.apply(allUsers.stream(), subscriptionUserFilterDto);
        List<User> actualUsers = userStream.toList();

        List<User> expectedUsers = allUsers.stream()
                .filter(u -> u.getId() == 2L)
                .toList();

        Assertions.assertEquals(actualUsers, expectedUsers);
    }

    @Test
    @DisplayName("Test empty result of user filter by Page/PageSize")
    void applyFalseSearch() {
        subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .page(100)
                .pageSize(5)
                .build();
        Stream<User> userStream = filter.apply(allUsers.stream(), subscriptionUserFilterDto);
        List<User> actualUsers = userStream.toList();

        Assertions.assertEquals(actualUsers.size(), 0);
    }

}