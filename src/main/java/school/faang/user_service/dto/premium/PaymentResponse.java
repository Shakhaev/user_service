package school.faang.user_service.dto.premium;

import school.faang.user_service.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.Currency;

public record PaymentResponse(
        PaymentStatus status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        Currency currency,
        String message
) {
}