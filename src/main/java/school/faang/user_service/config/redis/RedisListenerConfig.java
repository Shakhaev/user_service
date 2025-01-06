package school.faang.user_service.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import school.faang.user_service.message.consumer.BanUserEventListener;
import school.faang.user_service.message.consumer.UsersBanListener;

@Configuration
@RequiredArgsConstructor
public class RedisListenerConfig {

    private final UsersBanListener usersBanListener;

    private final BanUserEventListener banUserEventListener;

    @Value("${spring.data.redis.channels.users-ban-channel.name}")
    private String usersBanTopicName;

    @Bean
    public MessageListenerAdapter banUserEventListenerAdapter(BanUserEventListener banUserEventListener) {
        return new MessageListenerAdapter(banUserEventListener);
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);

        ChannelTopic usersBanTopic = new ChannelTopic(usersBanTopicName);
        MessageListenerAdapter usersBanMessageListener = new MessageListenerAdapter(usersBanListener);
        container.addMessageListener(usersBanMessageListener, usersBanTopic);

        ChannelTopic userBanTopic = new ChannelTopic(usersBanTopicName);
        MessageListenerAdapter userBanMessageListener = new MessageListenerAdapter(banUserEventListener);
        container.addMessageListener(userBanMessageListener, userBanTopic);

        return container;
    }
}
