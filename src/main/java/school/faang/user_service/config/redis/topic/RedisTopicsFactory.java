package school.faang.user_service.config.redis.topic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;

@Configuration
public class RedisTopicsFactory {
    @Value("${spring.data.redis.channel-topics.event-start.name}")
    private String eventStartTopicName;

    @Value("${redis.banner.topic}")
    private String userBanTopic;

    @Bean
    public Topic eventStartTopic() {
        return new ChannelTopic(eventStartTopicName);
    }

    @Bean
    public ChannelTopic userBanTopic() {
        return new ChannelTopic(userBanTopic);
    }
}
