package school.faang.user_service.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.events.RecommendationRequestedEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestedEventPublisherTest {
    @InjectMocks
    private RecommendationRequestedEventPublisher goalCompletedEventPublisher;

    @Test
    void getInstance() {
        assertEquals(RecommendationRequestedEvent.class, goalCompletedEventPublisher.getInstance());
    }
}
