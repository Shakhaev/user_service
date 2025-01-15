package school.faang.user_service.dto.premium;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record BuyPremiumDto(
        @NotBlank
        @JsonProperty("payment_method")
        String paymentMethod,
        @Positive
        int days,
        @Positive
        @JsonProperty("user_id")
        long userId
) {
}
