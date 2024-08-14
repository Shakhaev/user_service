package school.faang.user_service.config.redis.mentorship;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class MentorshipRedisConfig {

    @Value("${spring.data.redis.channels.mentorship_channel.name}")
    private String mentorshipChannelName;

    @Bean
    public ChannelTopic mentorshipTopic() {
        return new ChannelTopic(mentorshipChannelName);
    }
}
