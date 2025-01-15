package school.faang.user_service.rating.listeners.absctract;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import school.faang.user_service.dto.rating.RatingDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.rating.RatingHistory;
import school.faang.user_service.rating.ActionType;
import school.faang.user_service.rating.description.Descriptionable;
import school.faang.user_service.repository.UserRepository;

@RequiredArgsConstructor
public abstract class AbstractEventListener {
    protected final UserRepository userRepository;

    @EventListener
    public void handle(RatingDTO ratingDTO) {
        User user = new User();
        int points = ratingDTO.getPoints();
        ActionType actionType = ratingDTO.getActionType();
        Descriptionable descriptionable = ratingDTO.getDescriptionable();

        user.setRatingPoints(user.getRatingPoints() + points);
        user.getRatingHistories().add(
                new RatingHistory().builder()
                        .user(user)
                        .points(points)
                        .actionType(actionType)
                        .description(descriptionable.say(user))
                        .build()
        );

        userRepository.save(user);
    }
}
