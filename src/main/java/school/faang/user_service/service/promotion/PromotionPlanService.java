package school.faang.user_service.service.promotion;

import school.faang.user_service.dto.promotion.PromotionPlanDto;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionPlan;
import school.faang.user_service.enums.promotion.PromotionPlanType;

import java.util.List;

public interface PromotionPlanService {

    List<PromotionPlanDto> getPromotionPlans();

    PromotionPlan getPromotionPlanByName(PromotionPlanType promotionPlanType);
}
