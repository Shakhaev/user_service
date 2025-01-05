package school.faang.user_service.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.contact.ContactType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;
    private final UserMapper userMapper = new UserMapper();

    @Test
    void followUser() {
        SubscriptionController controller = new SubscriptionController(subscriptionService, userMapper);
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(4, 5))
                .thenReturn(false);
        Assertions.assertDoesNotThrow(() -> controller.followUser(4, 5));
    }

    @Test
    void followSameUser() {
        SubscriptionController controller = new SubscriptionController(subscriptionService, userMapper);
        Assertions.assertThrows(DataValidationException.class, () -> controller.followUser(4, 4),
                "FollowerId 4 and FolloweeId 4 cannot be the same");
    }

    @Test
    void followIsExist() {
        SubscriptionController controller = new SubscriptionController(subscriptionService, userMapper);
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(4, 5))
                .thenReturn(true);
        Assertions.assertThrows(DataValidationException.class, () -> controller.followUser(4, 5),
                "This subscription (4 - 5) already exists");
    }

    @Test
    void unfollowUser() {
        SubscriptionController controller = new SubscriptionController(subscriptionService, userMapper);
        Assertions.assertDoesNotThrow(() -> controller.unfollowUser(4, 5));
    }

    @Test
    void unfollowSameUser() {
        SubscriptionController controller = new SubscriptionController(subscriptionService, userMapper);
        Assertions.assertThrows(DataValidationException.class, () -> controller.unfollowUser(4, 4),
                "FollowerId 4 and FolloweeId 4 cannot be the same");
    }

    @Test
    void getFollowers() {
        SubscriptionController controller = new SubscriptionController(subscriptionService, userMapper);
        Stream<User> mockedUsers = IntStream.rangeClosed(0, 100)
                .boxed()
                .map(i -> {
                    User user = new User();
                    user.setId(i.longValue());
                    user.setUsername("user%d".formatted(i));
                    user.setEmail("user%d@email.com".formatted(i));
                    user.setAboutMe("About user%d".formatted(i));
                    user.setContacts(List.of(
                            new Contact(0, user, "Contact%d".formatted(i), ContactType.CUSTOM),
                            new Contact(1, user, "@user%d".formatted(i), ContactType.TELEGRAM)
                    ));
                    user.setCountry(new Country(1, "Russia", List.of()));
                    user.setCity("Moscow");
                    user.setPhone("+14560245628");
                    user.setSkills(List.of(
                            new Skill(0, "Skill1", List.of(), List.of(), List.of(), List.of(), LocalDateTime.now(),
                                    LocalDateTime.now())
                    ));
                    user.setExperience(5);
                    return user;
                });

        Mockito.when(subscriptionRepository.findByFolloweeId(Mockito.anyLong()))
                .thenReturn(mockedUsers);

        List<UserDto> expectedUsers = IntStream.range(0, 20)
                .boxed()
                .map(i -> new UserDto(i.longValue(), "user%d".formatted(i), "user%d@email.com".formatted(i)))
                .toList();

        UserFilterDto filter = new UserFilterDto();
        filter.setNamePattern("\\w+");
        filter.setAboutPattern("[\\w*\\s*]*");
        filter.setEmailPattern("\\w+@\\w+.\\w+");
        filter.setContactPattern("\\w+");
        filter.setCountryPattern("\\w+");
        filter.setCityPattern("\\w+");
        filter.setPhonePattern("\\+\\d+");
        filter.setSkillPattern("\\w+");
        filter.setExperienceMin(1);
        filter.setExperienceMax(10);
        filter.setPage(2);
        filter.setPageSize(10);

        List<UserDto> usersDto = controller.getFollowers(3L, filter);
        Assertions.assertEquals(expectedUsers, usersDto);
    }

    @Test
    void getFollowersCount() {
        SubscriptionController controller = new SubscriptionController(subscriptionService, userMapper);
        Mockito.when(subscriptionRepository.findFollowersAmountByFolloweeId(Mockito.anyLong()))
                .thenReturn(77);
        int expectedCount = 77;
        int actualCount = controller.getFollowersCount(3L);
        Assertions.assertEquals(expectedCount, actualCount);
    }
}