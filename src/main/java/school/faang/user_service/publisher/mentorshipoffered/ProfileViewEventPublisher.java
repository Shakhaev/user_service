package school.faang.user_service.publisher.mentorshipoffered;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.ProfileViewEvent;
import school.faang.user_service.publisher.MessagePublisher;

@Service
@RequiredArgsConstructor
public class ProfileViewEventPublisher implements MessagePublisher<ProfileViewEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic profileViewTopic;

    @Override
    public void publish(ProfileViewEvent message) {
        redisTemplate.convertAndSend(profileViewTopic.getTopic(), message);

    }
}
