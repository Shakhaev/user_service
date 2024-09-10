package school.faang.user_service.validator.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.premium.PaymentResponse;
import school.faang.user_service.entity.premium.PaymentStatus;
import school.faang.user_service.repository.premium.PremiumRepository;

@Component
@RequiredArgsConstructor
public class PremiumValidator {

    private final PremiumRepository premiumRepository;

    public void validateUser(long userId) {
        if (premiumRepository.existsByUserId(userId)) {
            throw new IllegalStateException("User with ID: %d already has premium access."
                    .formatted(userId));
        }
    }

    public void verifyPayment(PaymentResponse paymentResponseDto) {
        if (paymentResponseDto.status() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Payment with payment number: %d failed."
                    .formatted(paymentResponseDto.paymentNumber()));
        }
    }
}