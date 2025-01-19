package school.faang.user_service.message.broker.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaListeners {
    @KafkaListener(topics = "${spring.kafka.topic.user.profile_view}", groupId = "${spring.kafka.consumer.group-id}")
    public void method(String message) {
        log.info("########## MESSAGE: {}", message);
    }
}
