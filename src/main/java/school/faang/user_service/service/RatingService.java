package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.rating.LeaderTableDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.LeaderTableMapper;
import school.faang.user_service.rating.ActionType;
import school.faang.user_service.rating.publisher.UserEventPublisher;
import school.faang.user_service.repository.UserRepository;
import static school.faang.user_service.config.kafka.KafkaTopics.PAYMENT_PROMOTION_TOPIC;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingService {
    private final UserEventPublisher userEventPublisher;
    private final UserRepository userRepository;
    private final LeaderTableMapper leaderTableMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Queue<String> queueForMessages = new LinkedList<>();

    private static final String LEADERBOARD_KEY = "leaderboard";
    private static final int LEADERBOARD_LIMIT = 50;

    @KafkaListener(topics = PAYMENT_PROMOTION_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void successTransactionListener(String userIdStr) {
        log.info("Made the success payment!");
        queueForMessages.add(userIdStr);

        if (!queueForMessages.isEmpty()) {
            String userId = queueForMessages.poll();
            if (userId != null) {
                userEventPublisher.publishEvent(ActionType.PAYMENT_SUCCESS, Integer.parseInt(userId));
            }
        }
    }

    @Transactional
    public List<LeaderTableDto> getTableLeaders(int limit) {
        Set<Object> cachedUsers = redisTemplate.opsForZSet().reverseRange(LEADERBOARD_KEY, 0, limit - 1);

        if (cachedUsers == null || cachedUsers.isEmpty()) {
            syncLeaderboardWithDatabase();
            cachedUsers = redisTemplate.opsForZSet().reverseRange(LEADERBOARD_KEY, 0, limit - 1);
        }

        if (cachedUsers != null && cachedUsers.size() >= limit) {
            return cachedUsers.stream()
                    .map(userId -> leaderTableMapper.toDto(
                            userRepository.findById((Long) userId).orElseThrow()))
                    .toList();
        }

        List<User> users = userRepository.findAllById(
                cachedUsers.stream().map(userId -> (Long) userId).toList()
        );

        if (users.size() < limit) {
            int remaining = limit - users.size();

            int minRating = users.stream()
                    .mapToInt(User::getRatingPoints)
                    .min()
                    .orElse(Integer.MAX_VALUE);

            List<User> additionalUsers = userRepository.findTopByRatingBelowLimit(minRating, remaining);
            users.addAll(additionalUsers);

            additionalUsers.forEach(user -> redisTemplate.opsForZSet()
                    .add(LEADERBOARD_KEY, user.getId(), user.getRatingPoints()));
        }

        return users.stream()
                .map(leaderTableMapper::toDto)
                .sorted(Comparator.comparingInt(LeaderTableDto::ratingPoints).reversed())
                .toList();
    }

    private void syncLeaderboardWithDatabase() {
        List<User> topUsers = userRepository.findTopByOrderByRatingPointsDesc(LEADERBOARD_LIMIT);
        topUsers.forEach(user -> redisTemplate.opsForZSet()
                .add(LEADERBOARD_KEY, user.getId(), user.getRatingPoints()));
    }
}
