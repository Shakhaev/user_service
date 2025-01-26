package school.faang.user_service.listener.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import school.faang.user_service.service.user.UserService;

@Service
@RequiredArgsConstructor
public class PostEventListener {

    private final UserService userService;

    @KafkaListener(topics = "${spring.kafka.topics.post-topic-name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String event) {
        userService.saveUserToCache(Long.parseLong(event));
    }
}
