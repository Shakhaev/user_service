package school.faang.user_service.dto.promotion;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import school.faang.user_service.enums.promotion.Currency;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record PaymentRequest(
        @NotNull
        UUID paymentNumber,

        @Min(1)
        @NotNull
        BigDecimal amount,

        @NotNull
        Currency currency
) {
}
