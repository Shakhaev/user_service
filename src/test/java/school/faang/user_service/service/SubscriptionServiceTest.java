package school.faang.user_service.service;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SubscriptionServiceTest {
    @InjectMocks
    private SubscriptionService service;

    @Mock
    private SubscriptionRepository repository;

    @Test
    void testRepeatSubscription() {
        InitialData data = getData(true);
        assertThrows(DataValidationException.class, () -> service.followUser(data.followerId(), data.followeeId()));
    }

    @Test
    void testSubscription() {
        InitialData data = getData(false);
        service.followUser(data.followerId(), data.followeeId());
        verify(repository, times(1)).followUser(data.followerId(), data.followeeId());
    }

    @Test
    void testUnsubscription() {
        InitialData data = getData(true);
        service.unfollowUser(data.followerId(), data.followeeId());
        verify(repository, times(1)).unfollowUser(data.followerId(), data.followeeId());
    }

    // протестировать повторную отписку
    @Test
    void testRepeatUnsubscription() {
        InitialData data = getData(false);
        assertThrows(DataValidationException.class, () -> service.unfollowUser(data.followerId(), data.followeeId()));
    }

    private @NotNull InitialData getData(boolean isThereSub) {
        long followerId = 1L;
        long followeeId = 2L;
        when(repository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(isThereSub);
        return new InitialData(followerId, followeeId);
    }

    private record InitialData(long followerId, long followeeId) {
    }
}