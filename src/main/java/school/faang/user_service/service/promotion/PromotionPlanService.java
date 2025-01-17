package school.faang.user_service.service.promotion;

import school.faang.user_service.dto.promotion.PromotionPlanDto;
import school.faang.user_service.entity.promotion.PromotionPlan;

import java.util.List;

public interface PromotionPlanService {

    List<PromotionPlanDto> getPromotionPlans();

    PromotionPlan getPromotionPlanByName(String promotionPlanType);
}
