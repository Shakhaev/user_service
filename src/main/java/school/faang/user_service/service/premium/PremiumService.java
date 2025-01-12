package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.annotation.publisher.PublishEvent;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.service.payment.PaymentService;
import school.faang.user_service.service.user.UserDomainService;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumValidationService premiumValidationService;
    private final PremiumDomainService premiumDomainService;
    private final UserDomainService userDomainService;
    private final PaymentService paymentService;

    @PublishEvent(returnedType = Premium.class)
    @Transactional
    public Premium buyPremium(long userId, PremiumPeriod period) {
        log.info("User with id: {} buy a premium {} days subscription", userId, period.getDays());
        User user = userDomainService.findById(userId);
        premiumValidationService.validateUserForSubPeriod(userId, user);
        PaymentResponseDto paymentResponse = paymentService.sendPayment(period);
        premiumValidationService.checkPaymentResponse(paymentResponse, userId, period);
        Premium premium = Premium
                .builder()
                .user(user)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(period.getDays()))
                .build();
        return premiumDomainService.save(premium);
    }
}
