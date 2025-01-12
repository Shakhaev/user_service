package school.faang.user_service.service.premium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.payment.PaymentStatus;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.service.payment.PaymentService;
import school.faang.user_service.service.user.UserDomainService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.util.premium.PremiumFabric.getPaymentResponse;
import static school.faang.user_service.util.premium.PremiumFabric.getUser;

@ExtendWith(MockitoExtension.class)
class PremiumServiceTest {
    private static final long USER_ID = 1L;
    private static final PremiumPeriod PERIOD = PremiumPeriod.MONTH;
    private static final String MESSAGE = "test message";

    @Mock
    private PremiumDomainService premiumDomainService;

    @Mock
    private PremiumValidationService premiumValidationService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private UserDomainService userDomainService;

    @InjectMocks
    private PremiumService premiumService;

    @Test
    @DisplayName("Buy premium successful")
    void testBuyPremiumSuccessful() {
        User user = getUser(USER_ID);
        PaymentResponseDto successResponse = getPaymentResponse(PaymentStatus.SUCCESS, MESSAGE);
        when(userDomainService.findById(USER_ID)).thenReturn(user);
        when(paymentService.sendPayment(PERIOD)).thenReturn(successResponse);
        premiumService.buyPremium(USER_ID, PERIOD);

        verify(premiumDomainService).save(any(Premium.class));
        verify(premiumValidationService).validateUserForSubPeriod(USER_ID, user);
        verify(premiumValidationService)
                .checkPaymentResponse(any(PaymentResponseDto.class), any(Long.class), any(PremiumPeriod.class));
    }
}