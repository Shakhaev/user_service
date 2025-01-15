package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SubscriptionUserDto;
import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SubscriptionUserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.impl.SubscriptionServiceImpl;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    //@Mock
    //private SubscriptionRepository subscriptionRepository;
    //private SubscriptionRepository subscriptionRepository;
    //@InjectMocks
    //private SubscriptionServiceImpl subscriptionService;
    private SubscriptionRepository subscriptionRepositoryMock;
    private SubscriptionService subscriptionService;
    private SubscriptionFilter filterMock;
    private long followerId;
    private long followeeId;
    private List<User> allUsers;

    @BeforeEach
    public void init() {
        followerId = 1L;
        followeeId = 2L;
        allUsers = TestData.getUsers();

        subscriptionRepositoryMock = Mockito.mock(SubscriptionRepository.class);
        SubscriptionUserMapper mapperMock = Mockito.mock(SubscriptionUserMapper.class);
        filterMock = Mockito.mock(SubscriptionFilter.class);
        //SubscriptionFilter filter = new SubscriptionUserPageFilter();

        List<SubscriptionFilter> filters = List.of(filterMock);
        subscriptionService = new SubscriptionServiceImpl(subscriptionRepositoryMock, filters, mapperMock);

    }

    @Test
    @DisplayName("Follow To Another User")
    void testFollowOneUserByAnotherUser() {
        subscriptionService.followUser(followerId, followeeId);
        Mockito.verify(subscriptionRepositoryMock, Mockito.times(1))
                .followUser(followerId, followeeId);
    }

    @Test
    @DisplayName("Follow By Himself")
    void testFollowUserByHimself() {
        Mockito.when(subscriptionRepositoryMock.existsByFollowerIdAndFolloweeId(followerId, followerId))
                .thenThrow(new DataValidationException("!!"));
        Assert.assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followerId));
    }

    @Test
    @DisplayName("Unfollow Another User")
    void testUnfollowOneUserFromAnotherUser() {
        subscriptionService.unfollowUser(followerId, followeeId);
        Mockito.verify(subscriptionRepositoryMock, Mockito.times(1))
                .unfollowUser(followerId, followeeId);
    }

    @Test
    @DisplayName("Get Followers Count")
    void testGetFollowersCount() {
        subscriptionService.getFollowersCount(followeeId);
        Mockito.verify(subscriptionRepositoryMock, Mockito.times(1))
                .findFollowersAmountByFolloweeId(followeeId);
    }

    @Test
    @DisplayName("Get Following Count")
    void testGetFollowingCount() {
        subscriptionService.getFollowingCount(followerId);
        Mockito.verify(subscriptionRepositoryMock, Mockito.times(1))
                .findFolloweesAmountByFollowerId(followerId);
    }

    @Test
    @DisplayName("Get Followers")
    void testGetFollowers() {
        SubscriptionUserFilterDto subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .page(1)
                .pageSize(10)
                .build();

        Mockito.when(subscriptionRepositoryMock.findByFolloweeId(followeeId)).thenReturn(allUsers.stream());
        Mockito.when(filterMock.isApplicable(subscriptionUserFilterDto)).thenReturn(true);
        Mockito.when(filterMock.apply(any(), any())).thenReturn(allUsers.stream());

        List<SubscriptionUserDto> actualUsersDtos =
                subscriptionService.getFollowers(followeeId, subscriptionUserFilterDto);

        Assertions.assertEquals(3,actualUsersDtos.size());
     }

    @Test
    @DisplayName("Get Followees")
    void testGetFollowees() {
        SubscriptionUserFilterDto subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .page(1)
                .pageSize(10)
                .build();

        Mockito.when(subscriptionRepositoryMock.findByFolloweeId(followerId)).thenReturn(allUsers.stream());
        Mockito.when(filterMock.isApplicable(subscriptionUserFilterDto)).thenReturn(true);
        Mockito.when(filterMock.apply(any(), any())).thenReturn(allUsers.stream());

        List<SubscriptionUserDto> actualUsersDtos =
                subscriptionService.getFollowing(followerId, subscriptionUserFilterDto);

        Assertions.assertEquals(3,actualUsersDtos.size());
    }
}
