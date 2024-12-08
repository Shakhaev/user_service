package school.faang.user_service.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Objects;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.pool-config.max-idle}")
    private int maxIdle;

    @Value("${spring.data.redis.pool-config.max-total}")
    private int maxTotal;

    @Value("${spring.data.redis.pool-config.min-idle}")
    private int minIdle;

    @Value("${spring.data.redis.channels.follower-event-channel.name}")
    private String folowerTopic;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        JedisConnectionFactory factory = new JedisConnectionFactory(config);
        Objects.requireNonNull(factory.getPoolConfig()).setMaxIdle(maxIdle);
        factory.getPoolConfig().setMaxTotal(maxTotal);
        factory.getPoolConfig().setMinIdle(minIdle);

        return factory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public ChannelTopic followerEventChannel() {
        return new ChannelTopic(folowerTopic);
    }
}
