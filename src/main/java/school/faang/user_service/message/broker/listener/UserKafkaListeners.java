package school.faang.user_service.message.broker.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.message.UsersForBanMessage;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class UserKafkaListeners {
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @KafkaListener(topics = "${spring.kafka.topic.user.ban}", groupId = "${spring.kafka.publishers_group_ids.post_service}")
    public void usersForBan(String message) {
        UsersForBanMessage usersForBanMessage = readMessage(message, UsersForBanMessage.class);
        List<Long> userIds = usersForBanMessage.getUserIds();

        userIds.forEach(userService::bannedUser);
    }

    private <T> T readMessage(String message, Class<T> clazz) {
        try {
            return objectMapper.readValue(message, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
