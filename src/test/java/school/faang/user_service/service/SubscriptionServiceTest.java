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
    void testReSubscriptionFor() {
        InitialData data = getData(true);
        assertThrows(DataValidationException.class, () -> service.followUser(data.followerId(), data.followeeId()));
    }

    @Test
    void testSubscriptionFor() {
        InitialData data = getData(false);
        service.followUser(data.followerId(), data.followeeId());
        verify(repository, times(1)).followUser(data.followerId(), data.followeeId());
    }

    private @NotNull InitialData getData(boolean existsById) {
        long followerId = 1L;
        long followeeId = 2L;
        when(repository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(existsById);
        return new InitialData(followerId, followeeId);
    }

    private record InitialData(long followerId, long followeeId) {
    }
}