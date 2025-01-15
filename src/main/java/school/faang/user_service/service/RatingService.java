package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.rating.RatingDTO;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final ApplicationEventPublisher publisher;

    public void addPoints(RatingDTO ratingDTO) {
        publisher.publishEvent(ratingDTO);
    }
}
