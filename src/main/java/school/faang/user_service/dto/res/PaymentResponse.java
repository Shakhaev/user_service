package school.faang.user_service.dto.res;

import school.faang.user_service.common.Currency;
import school.faang.user_service.common.PaymentStatus;

import java.math.BigDecimal;

public record PaymentResponse(
        PaymentStatus status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        Currency currency,
        String message
) {
}
