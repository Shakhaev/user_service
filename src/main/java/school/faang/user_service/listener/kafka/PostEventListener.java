package school.faang.user_service.listener.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.UserService;

@Service
@RequiredArgsConstructor
public class PostEventListener {

    private final UserService userService;

    @CachePut(value = "users", key = "#event")
    @KafkaListener(topics = "${spring.kafka.topics.post-topic-name}", groupId = "${spring.kafka.consumer.group-id}")
    public User listen(String event) {
        return userService.getUserEntity(Long.parseLong(event));
    }
}
