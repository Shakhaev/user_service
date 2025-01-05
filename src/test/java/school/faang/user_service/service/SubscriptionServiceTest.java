package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SubscriptionUserDto;
import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SubscriptionUserMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.ArrayList;
import java.util.List;

import static school.faang.user_service.exception.MessageError.USER_ALREADY_HAS_THIS_FOLLOWER;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    SubscriptionRepository subscriptionRepository;

    @Spy
    SubscriptionUserMapperImpl subscriptionUserMapper;
    //Mappers.getMapper(SubscriptionUserMapper.class);
    @InjectMocks
    SubscriptionService subscriptionService;

    long followerId;
    long followeeId;

    List<User> followers = new ArrayList<>();
    List<User> followees = new ArrayList<>();


    @BeforeEach
    public void init() {
        followerId = 1L;
        followeeId = 2L;

        fillFollowersAndFollowees();


        //followers.add(user1);
        //followees.add(user1);
    }

    @Test
    @DisplayName("Follow To Another User")
    void testFollowOneUserByAnotherUser() {
        subscriptionService.followUser(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .followUser(followerId, followeeId);
    }

    @Test
    @DisplayName("Follow To Himself")
    void testFollowUserByHimself() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followerId))
                .thenThrow(new DataValidationException(USER_ALREADY_HAS_THIS_FOLLOWER));
        Assert.assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followerId));
    }

    @Test
    @DisplayName("Unfollow Another User")
    void testUnfollowOneUserFromAnotherUser() {
        subscriptionService.unfollowUser(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .unfollowUser(followerId, followeeId);
    }

    @Test
    @DisplayName("Get Followers Count")
    void testGetFollowersCount() {
        subscriptionService.getFollowersCount(followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findFollowersAmountByFolloweeId(followeeId);
    }

    @Test
    @DisplayName("Get Following Count")
    void testGetFollowingCount() {
        subscriptionService.getFollowingCount(followerId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findFolloweesAmountByFollowerId(followerId);
    }

    @Test
    @DisplayName("Test user filter by Name")
    void testFilterFollowersByName() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        SubscriptionUserFilterDto filter = new SubscriptionUserFilterDto();
        filter.setNamePattern("m.sha");
        List<SubscriptionUserDto> usersDtos = subscriptionService.getFollowing(followeeId, filter);

        List<SubscriptionUserDto> expectedUserDtos = followers.stream()
                .filter(u -> u.getId() != 3L)
                .map(subscriptionUserMapper::toDto)
                .toList();

        Assert.assertEquals(2, usersDtos.size());
        Assert.assertArrayEquals(expectedUserDtos.toArray(), usersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Empty Name")
    void testFilterFollowersByEmptyName() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        SubscriptionUserFilterDto filter = new SubscriptionUserFilterDto();
        filter.setNamePattern("");
        List<SubscriptionUserDto> usersDtos = subscriptionService.getFollowing(followeeId, filter);

        List<SubscriptionUserDto> expectedUserDtos = followers.stream()
                .map(subscriptionUserMapper::toDto)
                .toList();

        Assert.assertEquals(3, usersDtos.size());
        Assert.assertArrayEquals(expectedUserDtos.toArray(), usersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by About Me")
    void testFilterFollowersByAboutMe() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        SubscriptionUserFilterDto filter = new SubscriptionUserFilterDto();
        filter.setAboutPattern("I'm Masha");
        List<SubscriptionUserDto> usersDtos = subscriptionService.getFollowing(followeeId, filter);

        List<SubscriptionUserDto> expectedUserDtos = followers.stream()
                .filter(u -> u.getId() == 2L)
                .map(subscriptionUserMapper::toDto)
                .toList();

        Assert.assertEquals(1, usersDtos.size());
        Assert.assertArrayEquals(expectedUserDtos.toArray(), usersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Empty About Me")
    void testFilterFollowersByEmptyAboutMe() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        SubscriptionUserFilterDto filter = new SubscriptionUserFilterDto();
        filter.setAboutPattern(null);
        List<SubscriptionUserDto> usersDtos = subscriptionService.getFollowing(followeeId, filter);

        List<SubscriptionUserDto> expectedUserDtos = followers.stream()
                .map(subscriptionUserMapper::toDto)
                .toList();

        Assert.assertEquals(3, usersDtos.size());
        Assert.assertArrayEquals(expectedUserDtos.toArray(), usersDtos.toArray());
    }


    private void fillFollowersAndFollowees()
    {
        User user1 = User.builder()
                .city("Moscow")
                .active(true)
                .id(1L)
                .country(new Country(1L, "Country 1", new ArrayList<>()))
                .goals(new ArrayList<>())
                .contacts(new ArrayList<>())
                .aboutMe("I'm Misha")
                .email("misha@mail.ru")
                .username("misha")
                .build();
        followers.add(user1);
        followees.add(user1);

        User user2 = User.builder()
                .city("Piter")
                .active(false)
                .id(2L)
                .country(new Country(2L, "Country 2", new ArrayList<>()))
                .goals(new ArrayList<>())
                .contacts(new ArrayList<>())
                .aboutMe("I'm Masha")
                .email("masha@mail.ru")
                .username("masha")
                .build();

        followers.add(user2);
        followees.add(user2);

        User user3 = User.builder()
                .city("Kazan")
                .active(true)
                .id(3L)
                .country(new Country(3L, "Country 3", new ArrayList<>()))
                .goals(new ArrayList<>())
                .contacts(new ArrayList<>())
                .aboutMe("I'm Kesha")
                .email("kesha@mail.ru")
                .username("kesha")
                .build();

        followers.add(user3);
        followees.add(user3);
    }
}
