package school.faang.user_service.dto.promotion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.enums.promotion.Currency;
import school.faang.user_service.enums.promotion.PromotionPlanType;
import school.faang.user_service.enums.promotion.PromotionTariff;
import school.faang.user_service.enums.promotion.PromotionStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionResponseDto {

    private Long userId;

    private Long eventId;

    private BigDecimal amount;

    private Currency currency;

    private PromotionTariff promotionTariff;

    private PromotionPlanType promotionPlanType;

    private Integer remainingViews;

    private PromotionStatus status;

    private UUID paymentId;

}
