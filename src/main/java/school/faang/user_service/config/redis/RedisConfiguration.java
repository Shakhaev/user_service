package school.faang.user_service.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.util.Pair;
import school.faang.user_service.listener.ban.UserBanEventListener;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {
    private final RedisProperties redisProperties;
    private final ObjectMapper objectMapper;
    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private Integer port;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory(new RedisStandaloneConfiguration(host, port));
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

        return template;
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(List<Pair<MessageListenerAdapter, ChannelTopic>> requesters,
                                                        JedisConnectionFactory jedisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory);
        requesters.forEach(
                (requester) -> container.addMessageListener(requester.getFirst(), requester.getSecond())
        );

        return container;
    }

    @Bean
    public ChannelTopic userBanTopic() {
        return new ChannelTopic(redisProperties.getChannels().getUserBanChannel().getName());
    }

    @Bean
    public MessageListenerAdapter userBanMessageListener(UserBanEventListener userBanEventListener) {
        return new MessageListenerAdapter(userBanEventListener);
    }

    @Bean
    public Pair<MessageListenerAdapter, ChannelTopic> userBanPair(MessageListenerAdapter userBanMessageListener,
                                                                  ChannelTopic userBanTopic) {
        return Pair.of(userBanMessageListener, userBanTopic);
    }
}
