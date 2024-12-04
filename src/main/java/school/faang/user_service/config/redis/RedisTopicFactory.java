package school.faang.user_service.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class RedisTopicFactory {
    @Value("${spring.data.redis.topic.recommendation}")
    private String recommendationTopic;

    public ChannelTopic recommendationTopic() {
        return new ChannelTopic(recommendationTopic);
    }
}
