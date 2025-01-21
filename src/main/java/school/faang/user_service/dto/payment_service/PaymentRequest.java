package school.faang.user_service.dto.payment_service;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentRequest(
        long paymentNumber,
        BigDecimal amount,
        Currency currency) {}
