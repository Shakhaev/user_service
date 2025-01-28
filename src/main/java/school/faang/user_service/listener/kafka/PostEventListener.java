package school.faang.user_service.listener.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import school.faang.user_service.service.user.UserCacheService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostEventListener {

    private final UserCacheService userCacheService;

    @KafkaListener(topics = "${application.kafka.topics.post-topic-name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(List<Long> event) {
        userCacheService.saveUsersToCache(event);
    }
}
