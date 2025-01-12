package school.faang.user_service.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.UserSupplier;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionUserExperienceFilterTest {
    private SubscriptionUserFilterDto subscriptionUserFilterDto;
    private SubscriptionUserExperienceFilter filter = new SubscriptionUserExperienceFilter();
    private boolean isApplicableActual;
    private boolean isApplicableExpected;
    private List<User> users;
    List<User> expectedUsers;

    @BeforeEach
    void setUp() {
        users = UserSupplier.getUsers();
    }

    @Test
    @DisplayName("Test true applicability user filter by Experience")
    void isApplicableTest() {
        isApplicableExpected = true;
        subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .experienceMin(1)
                .experienceMax(100)
                .build();
        isApplicableActual = filter.isApplicable(subscriptionUserFilterDto);
        Assertions.assertEquals(isApplicableExpected, isApplicableActual);
    }

    @Test
    @DisplayName("Test false applicability user filter by Experience")
    void isNotApplicableTest() {
        isApplicableExpected = false;
        subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .experienceMin(100)
                .experienceMax(1)
                .build();
        isApplicableActual = filter.isApplicable(subscriptionUserFilterDto);
        Assertions.assertEquals(isApplicableExpected, isApplicableActual);

        subscriptionUserFilterDto = SubscriptionUserFilterDto.builder().build();
        isApplicableActual = filter.isApplicable(subscriptionUserFilterDto);
        Assertions.assertEquals(isApplicableExpected, isApplicableActual);
    }

    @Test
    @DisplayName("Test result of user filter by Experience")
    void applyTrueSearch() {
        subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .experienceMin(15)
                .experienceMax(40)
                .build();
        Stream<User> userStream = filter.apply(users.stream(), subscriptionUserFilterDto);
        List<User> actualUsers = userStream.toList();

        expectedUsers = users.stream()
                .filter(u -> (u.getId() == 2L || u.getId() == 3L))
                .toList();

        Assertions.assertEquals(actualUsers, expectedUsers);
    }

    @Test
    @DisplayName("Test empty result of user filter by Experience")
    void applyFalseSearch() {
        subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .experienceMin(100)
                .experienceMax(10)
                .build();
        Stream<User> userStream = filter.apply(users.stream(), subscriptionUserFilterDto);
        List<User> actualUsers = userStream.toList();

        Assertions.assertEquals(actualUsers.size(), 0);
    }
}