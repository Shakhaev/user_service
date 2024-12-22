package school.faang.user_service.controller.recommendation;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.RecommendationEvent;
import school.faang.user_service.publisher.RecommendationEventPublisher;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecommendationControllerTest {

    @Mock
    private RecommendationEventPublisher recommendationEventPublisher;

    @InjectMocks
    private RecommendationController recommendationController;

    @Test
    void testPublishRecommendation() {
        RecommendationEvent event = new RecommendationEvent(1L, 2L, 3L, LocalDateTime.now());

        recommendationController.publishRecommendation(event);

        verify(recommendationEventPublisher, times(1)).publishRecommendationEvent(event);
    }
}
