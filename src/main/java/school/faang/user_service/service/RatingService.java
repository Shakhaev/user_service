package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.rating.RatingDTO;
import school.faang.user_service.rating.ActionType;
import school.faang.user_service.rating.description.Descriptionable;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final ApplicationEventPublisher publisher;
    private static final Logger logger = LoggerFactory.getLogger(RatingService.class);

    public void addPoints(RatingDTO ratingDTO) {
        logger.info("Publishing event -> {}, {}, {}, {}", ratingDTO.getId(), ratingDTO.getActionType(), ratingDTO.getDescriptionable(), ratingDTO.getPoints());

        publisher.publishEvent(ratingDTO);
    }

    public void addRating(Descriptionable descriptionable, long userId,
                           int points, ActionType actionType) {
        RatingDTO ratingDTO = new RatingDTO(
                descriptionable,
                userId,
                points,
                actionType
        );

        addPoints(ratingDTO);
    }
}
