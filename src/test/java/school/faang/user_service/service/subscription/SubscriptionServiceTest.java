package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.subscription.FollowRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserAboutFilter;
import school.faang.user_service.filter.user.UserCityFilter;
import school.faang.user_service.filter.user.UserContactFilter;
import school.faang.user_service.filter.user.UserCountryFilter;
import school.faang.user_service.filter.user.UserEmailFilter;
import school.faang.user_service.filter.user.UserExperienceMaxFilter;
import school.faang.user_service.filter.user.UserExperienceMinFilter;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.filter.user.UserNameFilter;
import school.faang.user_service.filter.user.UserPhoneFilter;
import school.faang.user_service.filter.user.UserSkillFilter;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private SubscriptionService subscriptionService;

    private FollowRequestDto followRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        followRequestDto = new FollowRequestDto();
        followRequestDto.setFollowerId(1);
        followRequestDto.setFolloweeId(2);

        List<UserFilter> userFilters = new ArrayList<>();
        userFilters.add(new UserNameFilter());
        userFilters.add(new UserAboutFilter());
        userFilters.add(new UserEmailFilter());
        userFilters.add(new UserContactFilter());
        userFilters.add(new UserCountryFilter());
        userFilters.add(new UserCityFilter());
        userFilters.add(new UserPhoneFilter());
        userFilters.add(new UserSkillFilter());
        userFilters.add(new UserExperienceMinFilter());
        userFilters.add(new UserExperienceMaxFilter());

        subscriptionService = new SubscriptionService(subscriptionRepository, userFilters, userMapper);
    }

    @Test
    void testFollowUser_Success() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L)).thenReturn(false);

        subscriptionService.followUser(followRequestDto);

        verify(subscriptionRepository, times(1)).followUser(1L, 2L);
    }

    @Test
    void testFollowUser_AlreadyFollowing() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L)).thenReturn(true);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> subscriptionService.followUser(followRequestDto));
        assertEquals("You are already following this user", thrown.getMessage());
    }

    @Test
    void testFollowUser_SelfFollow() {
        followRequestDto = new FollowRequestDto();
        followRequestDto.setFollowerId(1);
        followRequestDto.setFolloweeId(1);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> subscriptionService.followUser(followRequestDto));
        assertEquals("You can`t follow yourself", thrown.getMessage());
    }

    @Test
    void testUnfollowUser_Success() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L)).thenReturn(true);

        subscriptionService.unfollowUser(followRequestDto);

        verify(subscriptionRepository, times(1)).unfollowUser(1L, 2L);
    }

    @Test
    void testUnfollowUser_SelfUnfollow() {
        followRequestDto = new FollowRequestDto();
        followRequestDto.setFollowerId(1);
        followRequestDto.setFolloweeId(1);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> subscriptionService.unfollowUser(followRequestDto));
        assertEquals("You can`t unfollow yourself", thrown.getMessage());
    }

    @Test
    void testGetFollowers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        UserDto userDto1 = userMapper.toDto(user1);
        UserFilterDto filters = new UserFilterDto();
        Stream<User> userStream = Stream.of(user1);
        when(subscriptionRepository.findFollowersByUserId(1L)).thenReturn(userStream);
        when(userMapper.toDto(user1)).thenReturn(userDto1);

        List<UserDto> followers = subscriptionService.getFollowers(1L, filters);

        assertEquals(1, followers.size());
        assertEquals("user1", followers.get(0).getUsername());
    }

    @Test
    void testGetFollowersCount() {
        when(subscriptionRepository.findFollowersAmountByUserId(1L)).thenReturn(5);

        int followersCount = subscriptionService.getFollowersCount(1L);

        assertEquals(5, followersCount);
    }

    @Test
    void testGetFollowing() {
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        UserDto userDto2 = userMapper.toDto(user2);
        UserFilterDto filters = new UserFilterDto();
        Stream<User> userStream = Stream.of(user2);
        when(subscriptionRepository.findFolloweesByUserId(1L)).thenReturn(userStream);
        when(userMapper.toDto(user2)).thenReturn(userDto2);

        List<UserDto> following = subscriptionService.getFollowing(1L, filters);

        assertEquals(1, following.size());
        assertEquals("user2", following.get(0).getUsername());
    }

    @Test
    void testGetFollowingCount() {
        when(subscriptionRepository.findFolloweesAmountByUserId(1L)).thenReturn(3);

        int followingCount = subscriptionService.getFollowingCount(1L);

        assertEquals(3, followingCount);
    }
}