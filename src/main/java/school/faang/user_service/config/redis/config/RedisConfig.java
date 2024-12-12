package school.faang.user_service.config.redis.config;



import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Value("${spring.data.redis.channel.search-appearance.name}")
    private String searchAppearanceTopicName;
    @Value("${spring.data.redis.channel.premium-bought.name}")
    private String premiumBoughtTopicName;
    private static final String CREATE_CHANNEL_LOG_MESSAGE = "Создание ChannelTopic для канала: {}";

    /**
     * Настройка подключения к Redis через LettuceConnectionFactory
     */
    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        log.info("Configuring Redis: Host={}, Port={}", redisProperties.getHost(), redisProperties.getPort());
        return new LettuceConnectionFactory(
                new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort()));
    }

    /**
     * Настройка топиков (каналов) для Pub/Sub
     */
//    @Bean
//    public ChannelTopic topicEventParticipation() {
//        return createChannel(redisProperties.getTopicEventParticipation());
//    }

//    @Bean
//    public ChannelTopic followerChannel() {
//        return createChannel(redisProperties.getFollowerChannel());
//    }

//    @Bean
//    public ChannelTopic unfollowerChannel() {
//        return createChannel(redisProperties.getUnfollowChannel());
//    }

    @Bean
    public ChannelTopic createAppearanceTopic() {
        return createChannel(searchAppearanceTopicName);
    }

    private ChannelTopic createChannel(String channelName) {
        log.info(CREATE_CHANNEL_LOG_MESSAGE, channelName);
        return new ChannelTopic(channelName);
    }

    /**
     * Настройка RedisTemplate для сериализации/десериализации
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        log.info("RedisTemplate configured successfully.");
        return template;
    }
    @Bean
    public ChannelTopic premiumBoughtTopic(@Value("${spring.data.redis.channel.premium-bought.name}") String channelName) {
        return new ChannelTopic(channelName);
    }

}

