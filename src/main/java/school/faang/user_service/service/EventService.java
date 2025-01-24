package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.PromotionServiceClient;
import school.faang.user_service.dto.event.EventResponse;
import school.faang.user_service.dto.promotion.EventPromotionRequest;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.util.ConverterUtil;
import school.faang.user_service.validator.UserValidator;

import java.util.List;

import static school.faang.user_service.config.KafkaConstants.EVENT_KEY;
import static school.faang.user_service.config.KafkaConstants.PAYMENT_PROMOTION_TOPIC;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository repository;

    private final EventRepository eventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ConverterUtil converterUtil;
    private final PromotionServiceClient promotionServiceClient;
    private final EventMapper eventMapper;
    private final UserValidator userValidator;

    @Transactional
    public void removeEvent(Long eventId) {
        repository.deleteById(eventId);
    }

    public void eventPromotion(EventPromotionRequest request) {
        validateEvent(request.eventId());
        userValidator.validateUser(request.userId());

        String message = converterUtil.convertToJson(request);
        kafkaTemplate.send(PAYMENT_PROMOTION_TOPIC, EVENT_KEY, message);
    }

    private void validateEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event with id " + eventId + " does not exist");
        }
    }

    public List<EventResponse> getPromotionEvents() {
        List<Long> eventIds = promotionServiceClient.getPromotionEvents();
        return eventIds.stream()
                .map(event -> {
                    validateEvent(event);
                    return eventMapper.toEventResponse(eventRepository.findById(event).get());
                })
                .toList();
    }
}
