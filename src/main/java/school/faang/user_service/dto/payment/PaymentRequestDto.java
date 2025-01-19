package school.faang.user_service.dto.payment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PaymentRequestDto {
    @NotBlank
    private String serviceType;
    @NotBlank
    private String plan;
    @NotBlank
    private String paymentMethod;
}
