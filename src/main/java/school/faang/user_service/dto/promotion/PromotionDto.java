package school.faang.user_service.dto.promotion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.enums.promotion.PromotionPlanType;
import school.faang.user_service.enums.promotion.PromotionStatus;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionDto {

    private Long userId;

    private Long eventId;

    private BigDecimal money;

    private PromotionPlanType promotionPlanType;

    private Integer remainingViews;

    private PromotionStatus status;

    private String paymentId;

}
