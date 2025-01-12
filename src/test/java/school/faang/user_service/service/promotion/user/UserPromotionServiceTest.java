package school.faang.user_service.service.promotion.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.payment.PaymentStatus;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.repository.promotion.UserPromotionRepository;
import school.faang.user_service.service.payment.PaymentService;
import school.faang.user_service.service.promotion.PromotionTaskService;
import school.faang.user_service.service.promotion.util.UserPromoBuilder;
import school.faang.user_service.service.user.UserDomainService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.util.premium.PremiumFabric.getPaymentResponse;
import static school.faang.user_service.util.promotion.PromotionFabric.buildActiveUserPromotions;
import static school.faang.user_service.util.promotion.PromotionFabric.buildUsersWithActivePromotion;
import static school.faang.user_service.util.promotion.PromotionFabric.getUser;
import static school.faang.user_service.util.promotion.PromotionFabric.getUsers;

@ExtendWith(MockitoExtension.class)
class UserPromotionServiceTest {
    private static final long USER_ID = 1;
    private static final PromotionTariff TARIFF = PromotionTariff.STANDARD;
    private static final String MESSAGE = "test message";
    private static final int NUMBER_OF_USERS = 3;
    private static final long LIMIT = 10;
    private static final long OFFSET = 0;

    @Mock
    private UserPromotionRepository userPromotionRepository;

    @Mock
    private UserDomainService userDomainService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private PromotionTaskService promotionTaskService;

    @Mock
    private UserPromoValidationService userPromoValidationService;

    @Spy
    private UserPromoBuilder userPromoBuilder;

    @InjectMocks
    private UserPromotionService userPromotionService;

    @Test
    @DisplayName("Successful buy user promotion")
    void testBuyPromotionSuccessful() {
        User user = getUser(USER_ID);
        PaymentResponseDto paymentResponse = getPaymentResponse(PaymentStatus.SUCCESS, MESSAGE);
        when(userDomainService.findById(USER_ID)).thenReturn(user);
        when(paymentService.sendPayment(TARIFF)).thenReturn(paymentResponse);
        userPromotionService.buyPromotion(USER_ID, TARIFF);

        verify(userPromoBuilder).buildUserPromotion(user, TARIFF);
        verify(userPromotionRepository).save(any(UserPromotion.class));
    }

    @Test
    @DisplayName("Get promoted users per page success")
    void testGetPromotedUsersBeforeAllPerPageSuccessful() {
        List<User> users = buildUsersWithActivePromotion(NUMBER_OF_USERS);
        List<UserPromotion> activePromotions = buildActiveUserPromotions(NUMBER_OF_USERS);
        when(userDomainService.findAllSortedByPromotedUsersPerPage(OFFSET, LIMIT)).thenReturn(users);
        when(userPromoValidationService.getActiveUserPromotions(users)).thenReturn(activePromotions);

        assertThat(userPromotionService.getPromotedUsersBeforeAllPerPage(OFFSET, LIMIT))
                .isEqualTo(users);
        verify(promotionTaskService).incrementUserPromotionViews(activePromotions);
    }

    @Test
    @DisplayName("Get promoted users per page with no active promotion when check then no call decrement")
    void testGetPromotedUsersBeforeAllPerPageEmptyActivePromotions() {
        List<User> users = getUsers(NUMBER_OF_USERS);
        List<UserPromotion> activePromotions = List.of();
        when(userDomainService.findAllSortedByPromotedUsersPerPage(OFFSET, LIMIT)).thenReturn(users);
        when(userPromoValidationService.getActiveUserPromotions(users)).thenReturn(activePromotions);

        assertThat(userPromotionService.getPromotedUsersBeforeAllPerPage(OFFSET, LIMIT))
                .isEqualTo(users);
        verify(promotionTaskService, never()).incrementUserPromotionViews(activePromotions);
    }
}