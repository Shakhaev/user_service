package school.faang.user_service.service.promotion.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.payment.PaymentStatus;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.exception.payment.UnsuccessfulEventPromotionPaymentException;
import school.faang.user_service.repository.promotion.EventPromotionRepository;
import school.faang.user_service.service.event.EventDomainService;
import school.faang.user_service.service.payment.PaymentService;
import school.faang.user_service.service.promotion.PromotionTaskService;
import school.faang.user_service.service.promotion.util.EventPromoBuilder;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventPromotionService {
    private final EventPromoValidationService promotionValidationService;
    private final EventPromotionRepository eventPromotionRepository;
    private final PromotionTaskService promotionTaskService;
    private final EventDomainService eventDomainService;
    private final EventPromoBuilder eventPromoBuilder;
    private final PaymentService paymentService;

    @Transactional
    public EventPromotion buyEventPromotion(long userId, long eventId, PromotionTariff tariff) {
        log.info("User with id: {} buy promotion tariff: {} for event id: {}", userId, tariff.toString(), eventId);
        Event event = eventDomainService.findById(eventId);
        promotionValidationService.checkEventForUserAndPromotion(userId, eventId, event);

        PaymentResponseDto paymentResponse = paymentService.sendPayment(tariff);
        if (!paymentResponse.status().equals(PaymentStatus.SUCCESS)) {
            throw new UnsuccessfulEventPromotionPaymentException(tariff.getNumberOfViews(), eventId, paymentResponse.message());
        }

        EventPromotion eventPromotion = eventPromoBuilder.buildEventPromotion(event, tariff);
        return eventPromotionRepository.save(eventPromotion);
    }

    @Transactional(readOnly = true)
    public List<Event> getPromotedEventsBeforeAllPerPage(Long offset, Long limit) {
        log.info("Get promoted events before all per page: {} - {}", offset, limit);
        List<Event> events = eventDomainService.findAllSortedByPromotedEventsPerPage(offset, limit);
        List<EventPromotion> activeEventPromotions = promotionValidationService.getActiveEventPromotions(events);

        if (!activeEventPromotions.isEmpty()) {
            promotionTaskService.batchDecrementEventPromotionViews(activeEventPromotions);
        }
        return events;
    }
}
