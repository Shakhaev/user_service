package school.faang.user_service.dto.paymentService;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateOrderDto(
        String plan,
        @JsonProperty("payment_method")
        String paymentMethod
) {
}
