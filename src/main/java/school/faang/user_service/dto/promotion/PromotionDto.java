package school.faang.user_service.dto.promotion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.promotion.PromotionPayment;
import school.faang.user_service.entity.promotion.PromotionPlan;
import school.faang.user_service.enums.promotion.PromotionStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionDto {

    private long id;

    private long userId;

    private long event_id;

    private PromotionPlan promotionPlan;

    private int remainingViews;

    private PromotionStatus status;

    private PromotionPayment promotionPayment;
}
