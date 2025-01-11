package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.contact.ContactType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    SubscriptionRepository subscriptionRepository = Mockito.mock(SubscriptionRepository.class);
    List<UserFilter> userFilters = new ArrayList<>();
    SubscriptionService subscriptionService = new SubscriptionService(subscriptionRepository, userFilters);

    @Test
    void followUser() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(4, 5))
                .thenReturn(false);
        Assertions.assertDoesNotThrow(() -> subscriptionService.followUser(4, 5));
        Mockito.verify(subscriptionRepository).existsByFollowerIdAndFolloweeId(4, 5);
    }

    @Test
    void followSameUser() {
        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.followUser(4, 4),
                "FollowerId 4 and FolloweeId 4 cannot be the same");
        Mockito.verify(subscriptionRepository, Mockito.never()).existsByFollowerIdAndFolloweeId(4, 4);
    }

    @Test
    void followIsExist() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(4, 5))
                .thenReturn(true);
        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.followUser(4, 5),
                "This subscription (4 - 5) already exists");
        Mockito.verify(subscriptionRepository).existsByFollowerIdAndFolloweeId(4, 5);
    }

    @Test
    void unfollowUser() {
        Assertions.assertDoesNotThrow(() -> subscriptionService.unfollowUser(4, 5));
        Mockito.verify(subscriptionRepository).unfollowUser(4, 5);
    }

    @Test
    void getFollowers() {
        Stream<User> mockedUsers = getMockedUsers(100);
        setUserFilters();
        Mockito.when(subscriptionRepository.findByFollowerId(Mockito.anyLong()))
                .thenReturn(mockedUsers);


        UserFilterDto filters = getFilter();

        List<User> users = subscriptionService.getFollowers(3L, filters);
        List<User> expectedUsers = getMockedUsers(20).toList();
        Assertions.assertTrue(expectedUsers.containsAll(users));
        Assertions.assertTrue(users.containsAll(expectedUsers));
    }

    @Test
    void getFollowersWithoutFilters() {
        Stream<User> mockedUsers = getMockedUsers(100);
        setUserFilters();
        Mockito.when(subscriptionRepository.findByFollowerId(Mockito.anyLong()))
                .thenReturn(mockedUsers);


        UserFilterDto filters = new UserFilterDto();

        List<User> users = subscriptionService.getFollowers(3L, filters);
        List<User> expectedUsers = getMockedUsers(100).toList();
        Assertions.assertTrue(expectedUsers.containsAll(users));
        Assertions.assertTrue(users.containsAll(expectedUsers));
    }

    @Test
    void getFollowersWithNameFilter() {
        Stream<User> mockedUsers = IntStream.range(0, 20)
                .boxed()
                .map(i -> User.builder()
                        .username("user%d".formatted(i))
                        .build());
        setUserFilters();

        Mockito.when(subscriptionRepository.findByFollowerId(Mockito.anyLong()))
                .thenReturn(mockedUsers);

        UserFilterDto filters = new UserFilterDto();
        filters.setNamePattern("r1");

        List<User> actualUsers = subscriptionService.getFollowers(3L, filters);

        List<User> expectedUsers = List.of(
                User.builder()
                        .username("user1")
                        .build(),
                User.builder()
                        .username("user10")
                        .build(),
                User.builder()
                        .username("user11")
                        .build(),
                User.builder()
                        .username("user12")
                        .build(),
                User.builder()
                        .username("user13")
                        .build(),
                User.builder()
                        .username("user14")
                        .build(),
                User.builder()
                        .username("user15")
                        .build(),
                User.builder()
                        .username("user16")
                        .build(),
                User.builder()
                        .username("user17")
                        .build(),
                User.builder()
                        .username("user18")
                        .build(),
                User.builder()
                        .username("user19")
                        .build()
        );
        Assertions.assertEquals(expectedUsers, actualUsers);
        Mockito.verify(subscriptionRepository).findByFollowerId(Mockito.anyLong());
    }

    @Test
    void getFollowersWithAboutFilter() {
        Stream<User> mockedUsers = IntStream.range(0, 20)
                .boxed()
                .map(i -> User.builder()
                        .aboutMe("About user%d".formatted(i))
                        .build());
        setUserFilters();

        Mockito.when(subscriptionRepository.findByFollowerId(Mockito.anyLong()))
                .thenReturn(mockedUsers);

        UserFilterDto filters = new UserFilterDto();
        filters.setAboutPattern("About user5");

        List<User> actualUsers = subscriptionService.getFollowers(3L, filters);

        List<User> expectedUsers = List.of(
                User.builder()
                        .aboutMe("About user5")
                        .build()
        );

        Assertions.assertEquals(expectedUsers, actualUsers);
        Mockito.verify(subscriptionRepository).findByFollowerId(Mockito.anyLong());
    }

    @Test
    void getFollowersWithCityFilter() {
        Stream<User> mockedUsers = IntStream.range(0, 21)
                .boxed()
                .map(i -> User.builder()
                        .city("city%d".formatted(i))
                        .build());
        setUserFilters();

        Mockito.when(subscriptionRepository.findByFollowerId(Mockito.anyLong()))
                .thenReturn(mockedUsers);

        UserFilterDto filters = new UserFilterDto();
        filters.setCityPattern("ty2");

        List<User> actualUsers = subscriptionService.getFollowers(3L, filters);

        List<User> expectedUsers = List.of(
                User.builder()
                        .city("city2")
                        .build(),
                User.builder()
                        .city("city20")
                        .build()
        );

        Assertions.assertEquals(expectedUsers, actualUsers);
        Mockito.verify(subscriptionRepository).findByFollowerId(Mockito.anyLong());
    }

    @Test
    void getFollowersWithContactFilter() {
        Stream<User> mockedUsers = IntStream.range(0, 20)
                .boxed()
                .map(i -> User.builder()
                        .contacts(List.of(
                                Contact.builder()
                                        .contact("contact%d".formatted(i))
                                        .build(),
                                Contact.builder()
                                        .contact("phone%d".formatted(i))
                                        .build()
                        ))
                        .build());

        setUserFilters();

        Mockito.when(subscriptionRepository.findByFollowerId(Mockito.anyLong()))
                .thenReturn(mockedUsers);

        UserFilterDto filters = new UserFilterDto();
        filters.setContactPattern("phone8");

        List<User> actualUsers = subscriptionService.getFollowers(3L, filters);

        List<User> expectedUsers = List.of(
                User.builder().contacts(List.of(
                        Contact.builder()
                                .contact("contact8")
                                .build(),
                        Contact.builder()
                                .contact("phone8")
                                .build()
                )).build()
        );
        Assertions.assertEquals(expectedUsers, actualUsers);
        Mockito.verify(subscriptionRepository).findByFollowerId(Mockito.anyLong());
    }

    @Test
    void getFollowersWithCountryFilter() {
        Stream<User> mockedUsers = IntStream.range(0, 21)
                .boxed()
                .map(i -> User.builder()
                        .country(Country.builder().title("country%d".formatted(i)).build())
                        .build());
        setUserFilters();

        Mockito.when(subscriptionRepository.findByFollowerId(Mockito.anyLong()))
                .thenReturn(mockedUsers);

        UserFilterDto filters = new UserFilterDto();
        filters.setCountryPattern("try2");

        List<User> actualUsers = subscriptionService.getFollowers(3L, filters);

        List<User> expectedUsers = List.of(
                User.builder()
                        .country(Country.builder()
                                .title("country2")
                                .build())
                        .build(),
                User.builder()
                        .country(Country.builder()
                                .title("country20")
                                .build())
                        .build()

        );
        Assertions.assertEquals(expectedUsers, actualUsers);
        Mockito.verify(subscriptionRepository).findByFollowerId(Mockito.anyLong());
    }

    @Test
    void getFollowersWithEmailFilter() {
        Stream<User> mockedUsers = IntStream.range(0, 20)
                .boxed()
                .map(i -> User.builder()
                        .email("user%d@gmail.com".formatted(i))
                        .build());
        setUserFilters();

        Mockito.when(subscriptionRepository.findByFollowerId(Mockito.anyLong()))
                .thenReturn(mockedUsers);

        UserFilterDto filters = new UserFilterDto();
        filters.setEmailPattern("6@gmail.com");

        List<User> actualUsers = subscriptionService.getFollowers(3L, filters);

        List<User> expectedUsers = List.of(
                User.builder()
                        .email("user6@gmail.com")
                        .build(),
                User.builder()
                        .email("user16@gmail.com")
                        .build()
        );

        Assertions.assertEquals(expectedUsers, actualUsers);
        Mockito.verify(subscriptionRepository).findByFollowerId(Mockito.anyLong());
    }

    @Test
    void getFollowersWithExperienceMaxFilter() {
        Stream<User> mockedUsers = IntStream.range(0, 20)
                .boxed()
                .map(i -> User.builder()
                        .experience(i)
                        .build());
        setUserFilters();

        Mockito.when(subscriptionRepository.findByFollowerId(Mockito.anyLong()))
                .thenReturn(mockedUsers);

        UserFilterDto filters = new UserFilterDto();
        filters.setExperienceMax(6);

        List<User> actualUsers = subscriptionService.getFollowers(3L, filters);

        List<User> expectedUsers = IntStream.range(0, 7)
                .boxed()
                .map(i -> User.builder()
                        .experience(i)
                        .build())
                .toList();

        Assertions.assertEquals(expectedUsers, actualUsers);
        Mockito.verify(subscriptionRepository).findByFollowerId(Mockito.anyLong());
    }

    @Test
    void getFollowersWithExperienceMinFilter() {
        Stream<User> mockedUsers = IntStream.range(0, 20)
                .boxed()
                .map(i -> User.builder()
                        .experience(i)
                        .build());
        setUserFilters();

        Mockito.when(subscriptionRepository.findByFollowerId(Mockito.anyLong()))
                .thenReturn(mockedUsers);

        UserFilterDto filters = new UserFilterDto();
        filters.setExperienceMin(6);

        List<User> actualUsers = subscriptionService.getFollowers(3L, filters);

        List<User> expectedUsers = IntStream.range(6, 20)
                .boxed()
                .map(i -> User.builder()
                        .experience(i)
                        .build())
                .toList();

        Assertions.assertEquals(expectedUsers, actualUsers);
        Mockito.verify(subscriptionRepository).findByFollowerId(Mockito.anyLong());
    }

    @Test
    void getFollowersWithPageFilter() {
        Stream<User> mockedUsers = IntStream.range(0, 20)
                .boxed()
                .map(i -> User.builder()
                        .username("user%d".formatted(i))
                        .build());
        setUserFilters();

        Mockito.when(subscriptionRepository.findByFollowerId(Mockito.anyLong()))
                .thenReturn(mockedUsers);

        UserFilterDto filters = new UserFilterDto();
        filters.setPage(3);
        filters.setPageSize(4);

        List<User> actualUsers = subscriptionService.getFollowers(3L, filters);

        List<User> expectedUsers = IntStream.range(0, 12)
                .boxed()
                .map(i -> User.builder()
                        .username("user%d".formatted(i))
                        .build())
                .toList();

        Assertions.assertEquals(expectedUsers, actualUsers);
        Mockito.verify(subscriptionRepository).findByFollowerId(Mockito.anyLong());
    }

    @Test
    void getFollowersWithPhoneFilter() {
        Stream<User> mockedUsers = IntStream.range(0, 20)
                .boxed()
                .map(i -> User.builder()
                        .phone("+79558454%d".formatted(i))
                        .build());
        setUserFilters();

        Mockito.when(subscriptionRepository.findByFollowerId(Mockito.anyLong()))
                .thenReturn(mockedUsers);

        UserFilterDto filters = new UserFilterDto();
        filters.setPhonePattern("543");

        List<User> actualUsers = subscriptionService.getFollowers(3L, filters);

        List<User> expectedUsers = List.of(
                User.builder()
                        .phone("+795584543")
                        .build()
        );

        Assertions.assertEquals(expectedUsers, actualUsers);
        Mockito.verify(subscriptionRepository).findByFollowerId(Mockito.anyLong());
    }

    @Test
    void getFollowersWithSkillFilter() {
        Stream<User> mockedUsers = IntStream.range(0, 20)
                .boxed()
                .map(i -> User.builder()
                        .skills(List.of(Skill.builder()
                                .title("skill%d".formatted(i))
                                .build()
                        ))
                        .build());
        setUserFilters();

        Mockito.when(subscriptionRepository.findByFollowerId(Mockito.anyLong()))
                .thenReturn(mockedUsers);

        UserFilterDto filters = new UserFilterDto();
        filters.setSkillPattern("SKILL");

        List<User> actualUsers = subscriptionService.getFollowers(3L, filters);

        List<User> expectedUsers = IntStream.range(0, 20)
                .boxed()
                .map(i -> User.builder()
                        .skills(List.of(Skill.builder()
                                .title("skill%d".formatted(i))
                                .build()
                        ))
                        .build())
                .toList();

        Assertions.assertEquals(expectedUsers, actualUsers);
        Mockito.verify(subscriptionRepository).findByFollowerId(Mockito.anyLong());
    }

    @Test
    void getFollowersCount() {
        Mockito.when(subscriptionRepository.findFolloweesAmountByFollowerId(Mockito.anyLong()))
                .thenReturn(77);

        int expectedCount = 77;
        int actualCount = subscriptionService.getFollowingCount(3L);

        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    void getFollowersCountNegative() {
        Mockito.when(subscriptionRepository.findFolloweesAmountByFollowerId(Mockito.anyLong()))
                .thenReturn(0);
        int expectedCount = 0;
        int actualCount = subscriptionService.getFollowingCount(3L);
        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    void getFollowing() {
        Stream<User> mockedUsers = getMockedUsers(100);
        setUserFilters();
        Mockito.when(subscriptionRepository.findByFolloweeId(Mockito.anyLong()))
                .thenReturn(mockedUsers);

        List<User> expectedUsers = getMockedUsers(20).toList();

        UserFilterDto filter = getFilter();

        List<User> users = subscriptionService.getFollowing(3L, filter);
        Assertions.assertEquals(expectedUsers, users);
    }

    @Test
    void getFollowingCount() {
        Mockito.when(subscriptionRepository.findFolloweesAmountByFollowerId(Mockito.anyLong()))
                .thenReturn(77);
        int expectedCount = 77;
        int actualCount = subscriptionService.getFollowingCount(3L);
        Assertions.assertEquals(expectedCount, actualCount);
        Mockito.verify(subscriptionRepository).findFolloweesAmountByFollowerId(3L);
    }

    @Test
    void getFollowingCountNegative() {
        Mockito.when(subscriptionRepository.findFolloweesAmountByFollowerId(Mockito.anyLong()))
                .thenReturn(0);
        int expectedCount = 0;
        int actualCount = subscriptionService.getFollowingCount(3L);
        Assertions.assertEquals(expectedCount, actualCount);
        Mockito.verify(subscriptionRepository).findFolloweesAmountByFollowerId(3L);
    }

    @BeforeEach
    public void setUserFilters() {
        userFilters.add(new UserAboutFilter());
        userFilters.add(new UserCityFilter());
        userFilters.add(new UserContactFilter());
        userFilters.add(new UserCountryFilter());
        userFilters.add(new UserEmailFilter());
        userFilters.add(new UserExperienceMinFilter());
        userFilters.add(new UserExperienceMaxFilter());
        userFilters.add(new UserNameFilter());
        userFilters.add(new UserPhoneFilter());
        userFilters.add(new UserSkillFilter());
        userFilters.add(new UserPageFilter());
    }

    private Stream<User> getMockedUsers(int count) {
        return IntStream.range(0, count)
                .boxed()
                .map(i -> User.builder()
                        .id(i.longValue())
                        .username("user%d".formatted(i))
                        .email("user%d@email.com".formatted(i))
                        .aboutMe("About user%d".formatted(i))
                        .contacts(List.of(
                                new Contact(0, new User(), "Contact%d".formatted(i), ContactType.CUSTOM),
                                new Contact(1, new User(), "@user%d".formatted(i), ContactType.TELEGRAM)
                        ))
                        .country(new Country(1, "Russia", List.of()))
                        .city("Moscow")
                        .phone("+14560245628")
                        .skills(List.of(
                                new Skill(0, "Skill1", List.of(), List.of(), List.of(), List.of(), null, null)
                        ))
                        .experience(5)
                        .build());

    }

    private UserFilterDto getFilter() {
        UserFilterDto filter = new UserFilterDto();
        filter.setNamePattern("user");
        filter.setAboutPattern("about");
        filter.setEmailPattern(".com");
        filter.setContactPattern("contact");
        filter.setCountryPattern("Russia");
        filter.setCityPattern("Moscow");
        filter.setPhonePattern("+14");
        filter.setSkillPattern("skill");
        filter.setExperienceMin(1);
        filter.setExperienceMax(10);
        filter.setPage(2);
        filter.setPageSize(10);

        return filter;
    }
}