package school.faang.user_service.service.promotion.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.exception.promotion.UserAlreadyHasPromotionException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static school.faang.user_service.util.promotion.PromotionFabric.ACTIVE_NUMBER_OF_VIEWS;
import static school.faang.user_service.util.promotion.PromotionFabric.buildActiveUserPromotion;
import static school.faang.user_service.util.promotion.PromotionFabric.buildNonActiveUserPromotion;
import static school.faang.user_service.util.promotion.PromotionFabric.buildUserWithActivePromotion;
import static school.faang.user_service.util.promotion.PromotionFabric.getUser;

class UserPromoValidationServiceTest {
    private static final long USER_ID = 1;
    private static final long PROMOTION_ID = 1;

    private final UserPromoValidationService userPromoValidationService = new UserPromoValidationService();

    @Test
    @DisplayName("Given already have active promotion user when check then throw exception")
    void testCheckUserForPromotionAlreadyHavePromotion() {
        User user = buildUserWithActivePromotion(USER_ID);
        assertThatThrownBy(() -> userPromoValidationService.checkUserForPromotion(user))
                .isInstanceOf(UserAlreadyHasPromotionException.class)
                .hasMessageContaining(new UserAlreadyHasPromotionException(user.getId(), ACTIVE_NUMBER_OF_VIEWS).getMessage());
    }

    @Test
    @DisplayName("Check user for promotion successful")
    void testCheckUserForPromotionSuccessful() {
        User user = getUser(USER_ID);
        user.setPromotions(List.of());
        userPromoValidationService.checkUserForPromotion(user);
    }

    @Test
    @DisplayName("Get active user promotion success")
    void testGetActiveUserPromotionSuccess() {
        UserPromotion activePromotion = buildActiveUserPromotion(PROMOTION_ID);
        UserPromotion unActivePromotion = buildNonActiveUserPromotion(PROMOTION_ID);
        User user = getUser(USER_ID);
        user.setPromotions(List.of(activePromotion, unActivePromotion));

        assertThat(userPromoValidationService.getActiveUserPromotion(user).orElseThrow())
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

        assertThat(userPromoValidationService.getActiveUserPromotions(users))
                .isEqualTo(activePromotions);
    }
}