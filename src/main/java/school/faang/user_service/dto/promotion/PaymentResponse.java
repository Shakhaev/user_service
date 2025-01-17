package school.faang.user_service.dto.promotion;

import school.faang.user_service.enums.promotion.Currency;
import school.faang.user_service.enums.promotion.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentResponse(
        PaymentStatus status,
        int verificationCode,
        UUID paymentNumber,
        BigDecimal amount,
        Currency currency,
        String message
) {
}