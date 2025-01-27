package school.faang.user_service.rating.listeners;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.rating.LeaderTableDto;
import school.faang.user_service.dto.rating.RatingDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.rating.RatingHistory;
import school.faang.user_service.mapper.LeaderTableMapper;
import school.faang.user_service.rating.ActionType;
import school.faang.user_service.rating.description.Descriptionable;
import school.faang.user_service.repository.UserRepository;

@RequiredArgsConstructor
@Component
public class RatingEventListener {
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(RatingEventListener.class);
    private final LeaderTableMapper leaderTableMapper;

    private static final String LEADERBOARD_KEY = "leaderboard";
    private static final int LEADERBOARD_LIMIT = 50;

    @EventListener
    @Transactional
    public void handle(RatingDto ratingDto) {
        logger.info("Got the info -> {}, {}, {}, {}",
                ratingDto.id(),
                ratingDto.actionType(),
                ratingDto.descriptionable(),
                ratingDto.points());

        User user = userRepository.findById(ratingDto.id()).get();
        int points = ratingDto.points();
        ActionType actionType = ratingDto.actionType();
        Descriptionable descriptionable = ratingDto.descriptionable();

        user.setRatingPoints(user.getRatingPoints() + points);
        user.getRatingHistories().add(
                RatingHistory.builder()
                        .user(user)
                        .points(points)
                        .actionType(actionType)
                        .description(descriptionable.say(user))
                        .build()
        );

        LeaderTableDto leaderTableDto = leaderTableMapper.toDto(user);

        if (isInTop(user)) {
            updateLeaderboardCache(leaderTableDto);
        }

        userRepository.save(user);
        logger.info("Saved the info -> {}, {}, {}, {}",
                ratingDto.id(),
                ratingDto.actionType(),
                ratingDto.descriptionable(),
                ratingDto.points());
    }

    private boolean isInTop(User user) {
        Double lowestScore = redisTemplate.opsForZSet().score(LEADERBOARD_KEY, LEADERBOARD_KEY + ":min");
        if (lowestScore == null || user.getRatingPoints() > lowestScore) {
            return true;
        }
        return false;
    }

    private void updateLeaderboardCache(LeaderTableDto leaderTableDto) {
        redisTemplate.opsForZSet().add(LEADERBOARD_KEY, leaderTableDto.id(), leaderTableDto.ratingPoints());
        redisTemplate.opsForZSet().removeRange(LEADERBOARD_KEY, 0, -LEADERBOARD_LIMIT - 1);
    }
}
