package school.faang.user_service.service.promotion;

import school.faang.user_service.dto.promotion.PromotionPlanDto;

import java.util.List;

public interface PromotionPlanService {

    List<PromotionPlanDto> getPromotionPlans();
}
