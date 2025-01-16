package school.faang.user_service.rating.listeners;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.rating.RatingDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.rating.RatingHistory;
import school.faang.user_service.rating.ActionType;
import school.faang.user_service.rating.description.Descriptionable;
import school.faang.user_service.repository.UserRepository;

@RequiredArgsConstructor
@Component
public class RatingEventListener {
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(RatingEventListener.class);

    @EventListener
    @Transactional
    public void handle(RatingDto ratingDTO) {
        logger.info("Got the info -> {}, {}, {}, {}", ratingDTO.id(), ratingDTO.actionType(), ratingDTO.descriptionable(), ratingDTO.points());

        User user = userRepository.findById(ratingDTO.id()).get();
        int points = ratingDTO.points();
        ActionType actionType = ratingDTO.actionType();
        Descriptionable descriptionable = ratingDTO.descriptionable();

        user.setRatingPoints(user.getRatingPoints() + points);
        user.getRatingHistories().add(
                RatingHistory.builder()
                        .user(user)
                        .points(points)
                        .actionType(actionType)
                        .description(descriptionable.say(user))
                        .build()
        );

        userRepository.save(user);
        logger.info("Saved the info -> {}, {}, {}, {}", ratingDTO.id(), ratingDTO.actionType(), ratingDTO.descriptionable(), ratingDTO.points());
    }
}
