package school.faang.user_service.service.promotion.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.event.exceptions.UserNotOwnerOfEventException;
import school.faang.user_service.exception.promotion.EventAlreadyHasPromotionException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static school.faang.user_service.util.promotion.PromotionFabric.ACTIVE_NUMBER_OF_VIEWS;
import static school.faang.user_service.util.promotion.PromotionFabric.buildEventWithActivePromotion;
import static school.faang.user_service.util.promotion.PromotionFabric.getEvent;
import static school.faang.user_service.util.promotion.PromotionFabric.getUser;

class EventPromoValidationServiceTest {
    private static final long USER_ID = 1;
    private static final long SECOND_USER_ID = 2;
    private static final long EVENT_ID = 1;

    private final EventPromoValidationService eventPromoValidationService = new EventPromoValidationService();

    @Test
    @DisplayName("Given not owner user when check then throw exception")
    void testCheckEventForUserAndPromotionUserNotOwner() {
        User user = getUser(USER_ID);
        Event event = getEvent(user);

        assertThatThrownBy(() ->
                eventPromoValidationService.checkEventForUserAndPromotion(SECOND_USER_ID, EVENT_ID, event))
                .isInstanceOf(UserNotOwnerOfEventException.class)
                .hasMessageContaining(new UserNotOwnerOfEventException(SECOND_USER_ID, EVENT_ID).getMessage());
    }

    @Test
    @DisplayName("Given event with active promotion when check then throw exception")
    void testCheckEventForUserAndPromotionActivePromotion() {
        User user = getUser(USER_ID);
        Event event = buildEventWithActivePromotion(EVENT_ID);
        event.setOwner(user);

        assertThatThrownBy(() -> eventPromoValidationService.checkEventForUserAndPromotion(USER_ID, EVENT_ID, event))
                .isInstanceOf(EventAlreadyHasPromotionException.class)
                .hasMessageContaining(new EventAlreadyHasPromotionException(EVENT_ID, ACTIVE_NUMBER_OF_VIEWS).getMessage());
    }

    @Test
    @DisplayName("Check event for user and promotion success")
    void testCheckEventForUserAndPromotionSuccess() {
        User user = getUser(USER_ID);
        Event event = getEvent(EVENT_ID);
        event.setOwner(user);
        event.setPromotions(List.of());
        eventPromoValidationService.checkEventForUserAndPromotion(USER_ID, EVENT_ID, event);
    }
}