package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.EventPromotionRequest;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.util.ConverterUtil;

import static school.faang.user_service.config.KafkaTopics.EVENT_KEY;
import static school.faang.user_service.config.KafkaTopics.PROMOTION_BOUGHT_TOPIC;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository repository;

    private final EventRepository eventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ConverterUtil converterUtil;
    private final UserService userService;

    @Transactional
    public void removeEvent(Long eventId) {
        repository.deleteById(eventId);
    }

    public void eventPromotion(EventPromotionRequest request) {
        validateEvent(request.eventId());
        userService.validateUser(request.userId());

        String message = converterUtil.convertToJson(request);
        kafkaTemplate.send(PROMOTION_BOUGHT_TOPIC, EVENT_KEY, message);
    }

    private void validateEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event with id " + eventId + " does not exist");
        }

}
