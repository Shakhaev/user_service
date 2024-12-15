package school.faang.user_service.config.jedis;

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
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import school.faang.user_service.listener.BanUserListener;

@Configuration
@RequiredArgsConstructor
public class JedisConfig {
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.channels.ban_user_topic.name}")
    private String banUserTopic;

    @Value("${spring.data.redis.channels.goal_completed_topic.name}")
    private String goalCompletedTopic;

    @Value("${spring.data.redis.channels.mentorship_accepted.name}")
    private String mentorshipAcceptedTopic;

    @Value("${spring.data.redis.channels.skill_acquired_topic.name}")
    private String skillAcquiredTopic;

    @Value("${spring.data.redis.channels.recommendation_received_topic.name}")
    private String recommendationReceivedTopic;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, Object.class));
        return template;
    }

    @Bean
    public MessageListenerAdapter banUserMessageListener(BanUserListener banUserListener) {
        return new MessageListenerAdapter(banUserListener);
    }

    @Bean
    public ChannelTopic banUserTopic() {
        return new ChannelTopic(banUserTopic);
    }

    @Bean
    public ChannelTopic goalCompletedTopic() {
        return new ChannelTopic(goalCompletedTopic);
    }
    @Bean
    public ChannelTopic skillAcquiredTopic(){
        return new ChannelTopic(skillAcquiredTopic);
    }

    @Bean
    public ChannelTopic mentorshipAcceptedTopic() {
        return new ChannelTopic(mentorshipAcceptedTopic);
    }

    @Bean ChannelTopic recommendationReceivedTopic() {
        return new ChannelTopic(recommendationReceivedTopic);
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(JedisConnectionFactory jedisConnectionFactory,
                                                        MessageListenerAdapter banUserMessageListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory);
        container.addMessageListener(banUserMessageListener, banUserTopic());
        return container;
    }

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        jedisPoolConfig.setMaxIdle(5);
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setJmxEnabled(false);

        return new JedisPool(jedisPoolConfig, host, port);
    }
}
