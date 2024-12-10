package school.faang.user_service.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final ObjectMapper objectMapper;

    @Bean
    RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

        return template;
    }

    @Bean
    public RedisMessageListenerContainer redisContainerConfig(
            RedisConnectionFactory connectionFactory,
            UserBanSubscriber userBanSubscriber
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        MessageListenerAdapter listenerAdapter = createListenerAdapter(userBanSubscriber);
        container.addMessageListener(listenerAdapter, new PatternTopic("user_ban"));

        return container;
    }

    private MessageListenerAdapter createListenerAdapter(UserBanSubscriber userBanSubscriber) {
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(userBanSubscriber);
        listenerAdapter.setDefaultListenerMethod("onMessage");

        return listenerAdapter;
    }
}