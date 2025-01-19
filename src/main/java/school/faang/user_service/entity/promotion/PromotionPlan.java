package school.faang.user_service.entity.promotion;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PromotionPlan {
    private Long id;
    private PromotionType name;
    private int impressions;
    private BigDecimal cost;
}
