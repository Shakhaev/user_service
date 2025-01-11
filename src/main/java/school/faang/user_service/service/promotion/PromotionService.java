package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.payment.PaymentStatus;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.exception.event.exceptions.EventNotFoundException;
import school.faang.user_service.exception.payment.UnsuccessfulEventPromotionPaymentException;
import school.faang.user_service.exception.payment.UnsuccessfulUserPromotionPaymentException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.promotion.EventPromotionRepository;
import school.faang.user_service.repository.promotion.UserPromotionRepository;
import school.faang.user_service.service.payment.PaymentService;
import school.faang.user_service.service.promotion.util.PromotionBuilder;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionService {
    private final UserPromotionRepository userPromotionRepository;
    private final EventPromotionRepository eventPromotionRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final PaymentService paymentService;
    private final PromotionTaskService promotionTaskService;
    private final PromotionValidationService promotionValidationService;
    private final PromotionBuilder promotionBuilder;

    @Transactional
    public UserPromotion buyPromotion(long userId, PromotionTariff tariff) {
        log.info("User with id: {} buy promotion tariff: {}", userId, tariff.toString());
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        promotionValidationService.checkUserForPromotion(user);

        PaymentResponseDto paymentResponse = paymentService.sendPayment(tariff);
        if (!paymentResponse.status().equals(PaymentStatus.SUCCESS)) {
            throw new UnsuccessfulUserPromotionPaymentException(tariff.getNumberOfViews(), userId, paymentResponse.message());
        }

        UserPromotion promotion = promotionBuilder.buildUserPromotion(user, tariff);
        return userPromotionRepository.save(promotion);
    }

    @Transactional
    public EventPromotion buyEventPromotion(long userId, long eventId, PromotionTariff tariff) {
        log.info("User with id: {} buy promotion tariff: {} for event id: {}", userId, tariff.toString(), eventId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        promotionValidationService.checkEventForUserAndPromotion(userId, eventId, event);

        PaymentResponseDto paymentResponse = paymentService.sendPayment(tariff);
        if (!paymentResponse.status().equals(PaymentStatus.SUCCESS)) {
            throw new UnsuccessfulEventPromotionPaymentException(tariff.getNumberOfViews(), eventId, paymentResponse.message());
        }

        EventPromotion eventPromotion = promotionBuilder.buildEventPromotion(event, tariff);
        return eventPromotionRepository.save(eventPromotion);
    }

    @Transactional(readOnly = true)
    public List<User> getPromotedUsersBeforeAllPerPage(int offset, int limit) {
        log.info("Get promoted users before all per page: {} - {}", offset, limit);
        List<User> users = userRepository.findAllSortedByPromotedUsersPerPage(offset, limit);
        List<UserPromotion> activeUserPromotions = promotionValidationService.getActiveUserPromotions(users);

        if (!activeUserPromotions.isEmpty()) {
            promotionTaskService.incrementUserPromotionViews(activeUserPromotions);
        }
        return users;
    }

    @Transactional(readOnly = true)
    public List<Event> getPromotedEventsBeforeAllPerPage(int offset, int limit) {
        log.info("Get promoted events before all per page: {} - {}", offset, limit);
        List<Event> events = eventRepository.findAllSortedByPromotedEventsPerPage(offset, limit);
        List<EventPromotion> activeEventPromotions = promotionValidationService.getActiveEventPromotions(events);

        if (!activeEventPromotions.isEmpty()) {
            promotionTaskService.batchDecrementEventPromotionViews(activeEventPromotions);
        }
        return events;
    }
}
