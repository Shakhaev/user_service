package school.faang.user_service.config.redis.listener;



import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PremiumEventListener implements MessageListener {

    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody());
            log.info("Received event from Redis: {}", json);

            // Обрабатывайте JSON, например, десериализация
            // Map<String, Object> eventData = objectMapper.readValue(json, Map.class);
            // log.info("Processed event data: {}", eventData);
        } catch (Exception e) {
            log.error("Error processing event", e);
        }
    }
}
