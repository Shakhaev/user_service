package school.faang.user_service.publisher.followerevent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.FollowerEvent;
import school.faang.user_service.publisher.MessagePublisher;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowerEventPublisher implements MessagePublisher<FollowerEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic followerTopic;

    @Override
    public void publish(FollowerEvent message) {
        log.info("FollowerSubscription -> publish: topic:{} message:{}",followerTopic.getTopic(), message);
        redisTemplate.convertAndSend(followerTopic.getTopic(), message);
    }
}