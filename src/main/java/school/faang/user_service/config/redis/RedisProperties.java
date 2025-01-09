package school.faang.user_service.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.redis")
public record RedisProperties(String host, int port, Channel channel) {

    public record Channel(String subscriptionChannel,
                          String recommendationChannel,
                          String goalChannel,
                          String userBanChannel,
                          String mentorshipRequest) {
    }
}