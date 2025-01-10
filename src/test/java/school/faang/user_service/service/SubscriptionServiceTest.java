package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import school.faang.user_service.dto.FollowingFeatureDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.UserWasNotFoundException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    @InjectMocks
    private SubscriptionService subscriptionService;
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    public void followUserAlreadyFollowed() {
        long followerId = 1L;
        long followeeId = 2L;
        FollowingFeatureDto followingFeatureDto = new FollowingFeatureDto(followerId, followeeId);

        User follower = mock(User.class);
        User followee = mock(User.class);

        Mockito.when(userRepository.findById(followerId)).thenReturn(Optional.of(follower));
        Mockito.when(userRepository.findById(followeeId)).thenReturn(Optional.of(followee));

        Mockito.when(follower.getFollowees().contains(followee)).thenReturn(true);

        var response = subscriptionService.followUser(followingFeatureDto);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        verify(userRepository, never()).save(any());
    }

    @Test
    public void followUserSameUser() {
        long userId = 1L;
        FollowingFeatureDto followingFeatureDto = new FollowingFeatureDto(userId, userId);
        Assert.assertThrows(
                DataValidationException.class,
                () -> subscriptionService.followUser(followingFeatureDto)
        );
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    public void followUserWasNotFound() {
        long followerId = 1L;
        long followeeId = 2L;
        FollowingFeatureDto followingFeatureDto = new FollowingFeatureDto(followerId, followeeId);
        Mockito.when(userRepository.findById(followerId)).thenReturn(Optional.empty());
        Assert.assertThrows(
                UserWasNotFoundException.class,
                () -> subscriptionService.followUser(followingFeatureDto)
        );
        verify(subscriptionRepository, never()).save(any());
    }
}
