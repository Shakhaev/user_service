package school.faang.user_service.client.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(
        long paymentNumber,

        @Min(1)
        @NotNull
        BigDecimal amount,

        @NotNull
        Currency currency
) {
}