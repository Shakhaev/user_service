package school.faang.user_service.service.premium;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.payment.UnsuccessfulUserPremiumBuyException;
import school.faang.user_service.exception.premium.UserAlreadyHasPremiumException;

import java.time.LocalDateTime;
import java.util.Optional;

import static school.faang.user_service.entity.payment.PaymentStatus.FAILED;

@Slf4j
@Service
public class PremiumValidationService {
    public void validateUserForSubPeriod(long userId, User user) {
        log.info("Verification of User with id: {} for buying premium subscription", userId);
        getActivePremium(user).ifPresent(premium -> {
            throw new UserAlreadyHasPremiumException(userId, premium.getEndDate());
        });
    }

    public void checkPaymentResponse(PaymentResponseDto paymentResponse, long userId, PremiumPeriod period) {
        log.info("Check payment response: {}", paymentResponse);
        if (paymentResponse.status() == FAILED) {
            throw new UnsuccessfulUserPremiumBuyException(period.getDays(), userId, paymentResponse.message());
        }
    }

    private Optional<Premium> getActivePremium(User user) {
        return user.getPremiums()
                .stream()
                .filter(premium -> premium.getEndDate().isAfter(LocalDateTime.now()))
                .findFirst();
    }
}
