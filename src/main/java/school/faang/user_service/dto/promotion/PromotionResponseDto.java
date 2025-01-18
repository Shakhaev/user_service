package school.faang.user_service.dto.promotion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.enums.promotion.PromotionPlanType;
import school.faang.user_service.enums.promotion.PromotionStatus;
import school.faang.user_service.enums.promotion.PromotionTariff;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionResponseDto {

    private Long userId;

    private Long eventId;

    private PromotionTariff tariff;

    private PromotionPlanType planType;

    private PromotionStatus status;

    private Integer usedViews;

    private UUID paymentId;

}
