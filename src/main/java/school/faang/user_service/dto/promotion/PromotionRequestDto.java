package school.faang.user_service.dto.promotion;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.dto.payment.PaymentRequestDto;
import school.faang.user_service.entity.promotion.PromotionType;
import school.faang.user_service.entity.promotion.TargetType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PromotionRequestDto {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Target type is required")
    private TargetType target;

    @NotNull(message = "Promotion plan is required")
    private PromotionType plan;

    @Min(value = 1, message = "Impressions limit must be at least 1")
    private int impressionsLimit;

    @Min(value = 0, message = "Current impressions cannot be negative")
    private int currentImpressions;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "Cost is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Cost must be greater than 0")
    private BigDecimal cost;

    @Valid
    private PaymentRequestDto paymentRequest;
}
