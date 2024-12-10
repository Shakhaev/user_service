package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.subscription.FollowerEvent;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.publisher.subscription.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.validator.subscription.SubscriptionValidator;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    @InjectMocks
    private SubscriptionService subscriptionService;
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private SubscriptionValidator subscriptionValidator;
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @Mock
    private List<UserFilter> filters;
    @Mock
    private UserFilter filter;
    @Mock
    private FollowerEventPublisher followerEventPublisher;

    private final long followerId = 4L;
    private final long followeeId = 3L;


    @Test
    public void testFollowUserSuccess() {
        subscriptionService.followUser(followerId, followeeId);

        verify(followerEventPublisher, times(1))
                .publish(new FollowerEvent(followerId, followeeId, LocalDateTime.now()));
        verify(subscriptionValidator, times(1))
                .validateUserIsTryingToCallHimself(followerId, followeeId);
        verify(subscriptionValidator, times(1))
                .validateUserAlreadyHasThisSubscription(followerId, followeeId);
        verify(subscriptionRepository, times(1))
                .followUser(followerId, followeeId);
    }

    @Test
    public void testUnfollowUserSuccess() {
        subscriptionService.unfollowUser(followerId, followeeId);

        verify(subscriptionValidator, times(1))
                .validateUserIsTryingToCallHimself(followerId, followeeId);
        verify(subscriptionRepository, times(1))
                .unfollowUser(followerId, followeeId);
    }

    @Test
    public void testGetFollowers() {
        filters = List.of(filter);
        subscriptionService.getFollowers(followeeId, new UserFilterDto());

        verify(subscriptionRepository, times(1)).findByFolloweeId(followeeId);
    }

    @Test
    public void testGetFollowing() {
        filters = List.of(filter);
        subscriptionService.getFollowing(followeeId, new UserFilterDto());

        verify(subscriptionRepository, times(1)).findByFollowerId(followeeId);
    }

    @Test
    public void testGetFollowersCount() {
        subscriptionService.getFollowersCount(followeeId);

        verify(subscriptionRepository, times(1)).findFollowersAmountByFolloweeId(followeeId);
    }

    @Test
    public void testGetFollowingCount() {
        subscriptionService.getFollowingCount(followerId);

        verify(subscriptionRepository, times(1)).findFolloweesAmountByFollowerId(followerId);
    }
}
