package school.faang.user_service.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.message.consumer.BanUserEventListener;
import school.faang.user_service.message.consumer.UsersBanListener;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final ObjectMapper objectMapper;

    private final UsersBanListener usersBanListener;

    private final BanUserEventListener banUserEventListener;

    @Value("${spring.data.redis.channels.users-ban-channel.name}")
    private String usersBanTopicName;

    @Bean
    public MessageListenerAdapter banUserEventListenerAdapter(BanUserEventListener banUserEventListener) {
        return new MessageListenerAdapter(banUserEventListener);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(banUserEventListener, banUserTopic());
        return container;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
        template.setValueSerializer(serializer);
        return template;
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);

        ChannelTopic usersBanTopic = new ChannelTopic(usersBanTopicName);
        MessageListenerAdapter usersBanMessageListener = new MessageListenerAdapter(usersBanListener);
        container.addMessageListener(usersBanMessageListener, usersBanTopic);

        return container;
    }

    @Bean("banUserTopic")
    public ChannelTopic banUserTopic() {
        return new ChannelTopic(usersBanTopicName);
    }
}
