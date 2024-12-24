package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.RedisScanException;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    @Value("${spring.data.redis.batch-size}")
    private static final int BATCH_SIZE = 1000;

    private final RedisTemplate<String, Object> lettuceRedisTemplate;

    public void saveValue(String key, Object value) {
        lettuceRedisTemplate.opsForValue().set(key, value);
    }

    public Object getValue(String key) {
        return lettuceRedisTemplate.opsForValue().get(key);
    }

    public void deleteKey(String key) {
        lettuceRedisTemplate.delete(key);
    }

    public Set<String> getKeysByPattern(String pattern) {
        Set<String> keys = new HashSet<>();
        try (RedisConnection connection = Objects.requireNonNull(lettuceRedisTemplate.getConnectionFactory()).getConnection()) {
            ScanOptions scanOptions = ScanOptions.scanOptions()
                    .match(pattern)
                    .count(BATCH_SIZE)
                    .build();
            try (var cursor = connection.scan(scanOptions)) {
                cursor.forEachRemaining(key -> keys.add(new String(key, StandardCharsets.UTF_8)));
            }
        } catch (Exception e) {
            throw new RedisScanException("Error during Redis SCAN operation");
        }
        return keys;
    }
}
