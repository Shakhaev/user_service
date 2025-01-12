package school.faang.user_service.service.promotion.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.payment.PaymentStatus;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.repository.promotion.EventPromotionRepository;
import school.faang.user_service.service.event.EventDomainService;
import school.faang.user_service.service.payment.PaymentService;
import school.faang.user_service.service.promotion.PromotionTaskService;
import school.faang.user_service.service.promotion.util.EventPromoBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.util.premium.PremiumFabric.getPaymentResponse;
import static school.faang.user_service.util.promotion.PromotionFabric.buildActiveEventPromotions;
import static school.faang.user_service.util.promotion.PromotionFabric.buildEventsWithActivePromotion;
import static school.faang.user_service.util.promotion.PromotionFabric.getEvent;
import static school.faang.user_service.util.promotion.PromotionFabric.getEvents;
import static school.faang.user_service.util.promotion.PromotionFabric.getUser;

@ExtendWith(MockitoExtension.class)
class EventPromoServiceTest {
    private static final long USER_ID = 1;
    private static final long EVENT_ID = 1;
    private static final PromotionTariff TARIFF = PromotionTariff.STANDARD;
    private static final String MESSAGE = "test message";
    private static final int NUMBER_OF_USERS = 3;
    private static final long LIMIT = 10;
    private static final long OFFSET = 0;

    @Spy
    private EventPromoBuilder eventPromoBuilder;

    @Mock
    private EventPromotionRepository eventPromotionRepository;

    @Mock
    private EventDomainService eventDomainService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private PromotionTaskService promotionTaskService;

    @Mock
    private EventPromoValidationService eventPromoValidationService;

    @InjectMocks
    private EventPromotionService eventPromotionService;

    @Test
    @DisplayName("Buy event promotion successful")
    void testBuyEventPromotionSuccessful() {
        User user = getUser(USER_ID);
        Event event = getEvent(user);
        PaymentResponseDto paymentResponse = getPaymentResponse(PaymentStatus.SUCCESS, MESSAGE);
        when(eventDomainService.findById(EVENT_ID)).thenReturn(event);
        when(paymentService.sendPayment(TARIFF)).thenReturn(paymentResponse);
        eventPromotionService.buyEventPromotion(USER_ID, EVENT_ID, TARIFF);

        verify(eventPromoBuilder).buildEventPromotion(event, TARIFF);
        verify(eventPromotionRepository).save(any(EventPromotion.class));
    }

    @Test
    @DisplayName("Get promoted events per page success")
    void testGetPromotedEventsBeforeAllPerPageSuccessful() {
        List<Event> events = buildEventsWithActivePromotion(NUMBER_OF_USERS);
        List<EventPromotion> eventPromotions = buildActiveEventPromotions(NUMBER_OF_USERS);
        when(eventDomainService.findAllSortedByPromotedEventsPerPage(OFFSET, LIMIT)).thenReturn(events);
        when(eventPromoValidationService.getActiveEventPromotions(events)).thenReturn(eventPromotions);

        assertThat(eventPromotionService.getPromotedEventsBeforeAllPerPage(OFFSET, LIMIT))
                .isEqualTo(events);
        verify(promotionTaskService).batchDecrementEventPromotionViews(eventPromotions);
    }

    @Test
    @DisplayName("Get promoted events per page with no active promotion when check then no call decrement")
    void testGetPromotedEventsBeforeAllPerPageEmptyActivePromotions() {
        List<Event> events = getEvents(NUMBER_OF_USERS);
        List<EventPromotion> activePromotions = List.of();
        when(eventDomainService.findAllSortedByPromotedEventsPerPage(OFFSET, LIMIT)).thenReturn(events);
        when(eventPromoValidationService.getActiveEventPromotions(events)).thenReturn(activePromotions);

        assertThat(eventPromotionService.getPromotedEventsBeforeAllPerPage(OFFSET, LIMIT))
                .isEqualTo(events);
        verify(promotionTaskService, never()).batchDecrementEventPromotionViews(activePromotions);
    }
}