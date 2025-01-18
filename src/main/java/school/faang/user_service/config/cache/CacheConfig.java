package school.faang.user_service.config.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("promotionPlans",
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put("promotionPlanByName",
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put("promotionPlanByPrice",
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)));
        return  RedisCacheManager
                .builder(redisConnectionFactory)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}