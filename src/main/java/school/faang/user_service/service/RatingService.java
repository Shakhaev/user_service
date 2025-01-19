package school.faang.user_service.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.AppConfig;
import school.faang.user_service.dto.rating.LeaderTableDto;
import school.faang.user_service.dto.rating.RatingDto;
import school.faang.user_service.dto.rating.UserComparingDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.LeaderTableMapper;
import school.faang.user_service.rating.ActionType;
import school.faang.user_service.rating.description.Descriptionable;
import school.faang.user_service.repository.UserRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final ApplicationEventPublisher publisher;
    private final AppConfig appConfig;
    private final UserRepository userRepository;
    private final LeaderTableMapper leaderTableMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Queue<String> queueForMessages = new LinkedList<>();
    private static final Logger logger = LoggerFactory.getLogger(RatingService.class);

    private static final String LEADERBOARD_KEY = "leaderboard";
    private static final int LEADERBOARD_LIMIT = 100;

    @KafkaListener(topics = "success_payment", groupId = "user-service-group")
    public void successTransactionListener(String userIdStr) {
        logger.info("Made the success payment!");
        queueForMessages.add(userIdStr);

        if (!queueForMessages.isEmpty()) {
            String userId = queueForMessages.poll();
            if (userId != null) {
                RatingDto ratingDto = RatingDto.builder()
                        .descriptionable((user) -> "made the success payment" + "made -> " + user.getId())
                        .points(appConfig.getActiveTransaction())
                        .actionType(ActionType.ACTIVE)
                        .id(Integer.parseInt(userId))
                        .build();
                addPoints(ratingDto);
            }
        }
    }

    @Transactional
    public List<LeaderTableDto> getTableLeaders(int limit, UserComparingDto userComparingDto) {
        Set<Object> topUsers = redisTemplate.opsForZSet().reverseRange(LEADERBOARD_KEY, 0, limit - 1);

        if (topUsers == null || topUsers.isEmpty()) {
            syncLeaderboardWithDatabase(limit);
            topUsers = redisTemplate.opsForZSet().reverseRange(LEADERBOARD_KEY, 0, limit - 1);
        }

        if (topUsers == null || topUsers.size() < limit) {
            List<User> additionalUsers = userRepository.findTopByOrderByRatingPointsDesc(limit);
            return additionalUsers.stream()
                    .map(leaderTableMapper::toDto)
                    .toList();
        }

        return topUsers.stream()
                .map(userId -> {
                    User user = userRepository.findById((Long) userId)
                            .orElseThrow(() -> new IllegalArgumentException("User was not found!"));
                    return leaderTableMapper.toDto(user);
                })
                .toList();
    }

    private void syncLeaderboardWithDatabase(int limit) {
        int adjustedLimit = Math.min(limit, LEADERBOARD_LIMIT);

        Set<Object> cachedUsers = redisTemplate.opsForZSet().reverseRange(LEADERBOARD_KEY, 0, adjustedLimit - 1);

        if (cachedUsers != null && cachedUsers.size() < adjustedLimit) {
            int remainingLimit = adjustedLimit - cachedUsers.size();
            List<User> additionalUsers = userRepository.findTopByOrderByRatingPointsDesc(remainingLimit);

            additionalUsers.forEach(user -> redisTemplate.opsForZSet()
                    .add(LEADERBOARD_KEY, user.getId(), user.getRatingPoints()));
        }
    }

    public void addPoints(RatingDto ratingDTO) {
        logger.info("Publishing event -> {}, {}, {}, {}", ratingDTO.id(), ratingDTO.actionType(), ratingDTO.descriptionable(), ratingDTO.points());

        publisher.publishEvent(ratingDTO);
    }

    public void addRating(Descriptionable descriptionable, long userId,
                           int points, ActionType actionType) {
        RatingDto ratingDTO = new RatingDto(
                descriptionable,
                userId,
                points,
                actionType
        );

        addPoints(ratingDTO);
    }
}
