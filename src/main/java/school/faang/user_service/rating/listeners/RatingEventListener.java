package school.faang.user_service.rating.listeners;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.rating.RatingDTO;
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
    public void handle(RatingDTO ratingDTO) {
        // тут грубо вызываю get, потому что до передачи сюда id уже были произведены проверки на пользователя.
        logger.info("Got the info -> {}, {}, {}, {}", ratingDTO.getId(), ratingDTO.getActionType(), ratingDTO.getDescriptionable(), ratingDTO.getPoints());

        User user = userRepository.findById(ratingDTO.getId()).get();
        int points = ratingDTO.getPoints();
        ActionType actionType = ratingDTO.getActionType();
        Descriptionable descriptionable = ratingDTO.getDescriptionable();

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
        logger.info("Saved the info -> {}, {}, {}, {}", ratingDTO.getId(), ratingDTO.getActionType(), ratingDTO.getDescriptionable(), ratingDTO.getPoints());
    }
}
