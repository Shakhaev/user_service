package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.exception.event.exceptions.UserNotOwnerOfEventException;
import school.faang.user_service.exception.promotion.EventAlreadyHasPromotionException;
import school.faang.user_service.exception.promotion.UserAlreadyHasPromotionException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static school.faang.user_service.util.promotion.PromotionFabric.ACTIVE_NUMBER_OF_VIEWS;
import static school.faang.user_service.util.promotion.PromotionFabric.buildActiveUserPromotion;
import static school.faang.user_service.util.promotion.PromotionFabric.buildEventWithActivePromotion;
import static school.faang.user_service.util.promotion.PromotionFabric.buildNonActiveUserPromotion;
import static school.faang.user_service.util.promotion.PromotionFabric.buildUserWithActivePromotion;
import static school.faang.user_service.util.promotion.PromotionFabric.getEvent;
import static school.faang.user_service.util.promotion.PromotionFabric.getUser;

class PromotionValidationServiceTest {
    private static final long USER_ID = 1;
    private static final long SECOND_USER_ID = 2;
    private static final long EVENT_ID = 1;
    private static final long PROMOTION_ID = 1;

    private final PromotionValidationService promotionValidationService = new PromotionValidationService();

    @Test
    @DisplayName("Given already have active promotion user when check then throw exception")
    void testCheckUserForPromotionAlreadyHavePromotion() {
        User user = buildUserWithActivePromotion(USER_ID);
        assertThatThrownBy(() -> promotionValidationService.checkUserForPromotion(user))
                .isInstanceOf(UserAlreadyHasPromotionException.class)
                .hasMessageContaining(new UserAlreadyHasPromotionException(user.getId(), ACTIVE_NUMBER_OF_VIEWS).getMessage());
    }

    @Test
    @DisplayName("Check user for promotion successful")
    void testCheckUserForPromotionSuccessful() {
        User user = getUser(USER_ID);
        user.setPromotions(List.of());
        promotionValidationService.checkUserForPromotion(user);
    }

    @Test
    @DisplayName("Get active user promotion success")
    void testGetActiveUserPromotionSuccess() {
        UserPromotion activePromotion = buildActiveUserPromotion(PROMOTION_ID);
        UserPromotion unActivePromotion = buildNonActiveUserPromotion(PROMOTION_ID);
        User user = getUser(USER_ID);
        user.setPromotions(List.of(activePromotion, unActivePromotion));

        assertThat(promotionValidationService.getActiveUserPromotion(user).orElseThrow())
                .isEqualTo(activePromotion);
    }

    @Test
    @DisplayName("Get active user promotions success")
    void testGetActiveUserPromotionsSuccess() {
        UserPromotion activePromotion = buildActiveUserPromotion(PROMOTION_ID);
        UserPromotion unActivePromotion = buildNonActiveUserPromotion(PROMOTION_ID);
        User user = getUser(USER_ID);
        user.setPromotions(List.of(activePromotion, unActivePromotion));
        List<User> users = List.of(user, user, user);
        List<UserPromotion> activePromotions = List.of(activePromotion, activePromotion, activePromotion);

        assertThat(promotionValidationService.getActiveUserPromotions(users))
                .isEqualTo(activePromotions);
    }

    @Test
    @DisplayName("Given not owner user when check then throw exception")
    void testCheckEventForUserAndPromotionUserNotOwner() {
        User user = getUser(USER_ID);
        Event event = getEvent(user);

        assertThatThrownBy(() ->
                promotionValidationService.checkEventForUserAndPromotion(SECOND_USER_ID, EVENT_ID, event))
                .isInstanceOf(UserNotOwnerOfEventException.class)
                .hasMessageContaining(new UserNotOwnerOfEventException(SECOND_USER_ID, EVENT_ID).getMessage());
    }

    @Test
    @DisplayName("Given event with active promotion when check then throw exception")
    void testCheckEventForUserAndPromotionActivePromotion() {
        User user = getUser(USER_ID);
        Event event = buildEventWithActivePromotion(EVENT_ID);
        event.setOwner(user);

        assertThatThrownBy(() -> promotionValidationService.checkEventForUserAndPromotion(USER_ID, EVENT_ID, event))
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
        promotionValidationService.checkEventForUserAndPromotion(USER_ID, EVENT_ID, event);
    }
}