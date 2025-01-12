package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
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
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SubscriptionUserMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.impl.*;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static school.faang.user_service.exception.MessageError.USER_ALREADY_HAS_THIS_FOLLOWER;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Spy
    private SubscriptionUserMapperImpl subscriptionUserMapper;
    //@Mock
    private List<SubscriptionFilter> subscriptionFilters;
    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;
    private long followerId;
    private long followeeId;
    private List<User> followers = new ArrayList<>();
    private List<User> followees = new ArrayList<>();
    //private SubscriptionUserFilterDto filter = new SubscriptionUserFilterDto();
    private List<SubscriptionUserDto> filteredUsersDtos;
    private List<SubscriptionUserDto> expectedUserDtos;
    private SubscriptionUserFilterDto filter = SubscriptionUserFilterDto.builder().build();

    @BeforeEach
    public void init() {
        followerId = 1L;
        followeeId = 2L;
        //fillFollowersAndFollowees();

        //SubscriptionFilter filter = mock(SubscriptionFilter.class);
        //List<SubscriptionFilter> subscriptionFilters = new ArrayList<>(List.of(filter));


        subscriptionFilters = new ArrayList<>();
        //subscriptionFilters.add(new SubscriptionUserDefaultFilter());
        subscriptionFilters.add(new SubscriptionUserNameFilter());
        subscriptionFilters.add(new SubscriptionUserAboutFilter());
        subscriptionFilters.add(new SubscriptionUserCityFilter());
        subscriptionFilters.add(new SubscriptionUserContactFilter());
        subscriptionFilters.add(new SubscriptionUserCountryFilter());
        subscriptionFilters.add(new SubscriptionUserEmailFilter());
        subscriptionFilters.add(new SubscriptionUserExperienceFilter());
        subscriptionFilters.add(new SubscriptionUserPhoneFilter());
        subscriptionFilters.add(new SubscriptionUserSkillFilter());
        subscriptionFilters.add(new SubscriptionUserPageFilter());

        subscriptionService = new SubscriptionServiceImpl(subscriptionRepository,
                subscriptionFilters, subscriptionUserMapper);
    }

    @Test
    @DisplayName("Follow To Another User")
    void testFollowOneUserByAnotherUser() {
        subscriptionService.followUser(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .followUser(followerId, followeeId);
    }

    @Test
    @DisplayName("Follow By Himself")
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
        Mockito.when(subscriptionRepository.findByFolloweeId(anyLong()))
                .thenReturn(followers.stream());

        filter.setNamePattern("m.sha");
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .filter(u -> u.getId() != 3L)
                .map(subscriptionUserMapper::toSubscriptionUserDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Empty Name")
    void testFilterFollowersByEmptyName() {
        Mockito.when(subscriptionRepository.findByFolloweeId(anyLong()))
                .thenReturn(followers.stream());

        filter.setNamePattern("");

        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .map(subscriptionUserMapper::toSubscriptionUserDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by About Me")
    void testFilterFollowersByAboutMe() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setAboutPattern("I'm Masha");
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .filter(u -> u.getId() == 2L)
                .map(subscriptionUserMapper::toSubscriptionUserDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Empty Email")
    void testFilterFollowersByEmptyEmail() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setEmailPattern("");
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .map(subscriptionUserMapper::toSubscriptionUserDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Email")
    void testFilterFollowersByEmail() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setEmailPattern("misha@mail.ru");
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .filter(u -> u.getId() == 1L)
                .map(subscriptionUserMapper::toSubscriptionUserDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Empty About Me")
    void testFilterFollowersByEmptyAboutMe() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());


        filter.setAboutPattern(null);
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .map(subscriptionUserMapper::toSubscriptionUserDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by City")
    void testFilterFollowersByCity() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setCityPattern("Moscow");
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .filter(u -> u.getId() == 1L)
                .map(subscriptionUserMapper::toSubscriptionUserDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Empty City")
    void testFilterFollowersByEmptyCity() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setCityPattern(null);
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .map(subscriptionUserMapper::toSubscriptionUserDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Phone")
    void testFilterFollowersByPhone() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setPhonePattern("456");
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .filter(u -> u.getId() == 2L)
                .map(subscriptionUserMapper::toSubscriptionUserDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Empty Phone")
    void testFilterFollowersByEmptyPhone() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setCityPattern(null);
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .map(subscriptionUserMapper::toSubscriptionUserDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Country")
    void testFilterFollowersByCountry() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setCountryPattern("China");
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .filter(u -> u.getId() == 3L)
                .map(subscriptionUserMapper::toSubscriptionUserDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Empty Country")
    void testFilterFollowersByEmptyCountry() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setCountryPattern(null);
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .map(subscriptionUserMapper::toSubscriptionUserDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Empty Skills")
    void testFilterFollowersByEmptySkills() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setCityPattern(null);
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .map(subscriptionUserMapper::toSubscriptionUserDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by 1 of Skills")
    void testFilterFollowersByOneSkills() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setSkillPattern("Skill 1");
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .filter(u -> u.getId() == 1L)
                .map(subscriptionUserMapper::toSubscriptionUserDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by 2 of Skills")
    void testFilterFollowersByTwoSkills() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setSkillPattern("Skill 3");
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .filter(u -> (u.getId() == 2L || u.getId() == 3L))
                .map(subscriptionUserMapper::toSubscriptionUserDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Empty Contacts")
    void testFilterFollowersByEmptyContacts() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setContactPattern(null);
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .map(subscriptionUserMapper::toSubscriptionUserDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by 1 of Contacts")
    void testFilterFollowersByOneContact() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setContactPattern("Contact 1");
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .filter(u -> u.getId() == 1L)
                .map(subscriptionUserMapper::toSubscriptionUserDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by 2 of Contacts")
    void testFilterFollowersByTwoContacts() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setContactPattern("Contact 3");
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .filter(u -> (u.getId() == 2L || u.getId() == 3L))
                .map(subscriptionUserMapper::toSubscriptionUserDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }



}
