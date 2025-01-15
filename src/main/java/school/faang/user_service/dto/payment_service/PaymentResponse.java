package school.faang.user_service.dto.payment_service;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentResponse(
        PaymentStatus status,
        int verificationCode,
        long paymentNumber,
        BigDecimal price,
        Currency currency,
        String message) {}
