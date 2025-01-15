package school.faang.user_service.repository.promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.promotion.PromotionPlan;

public interface PromotionPlanRepository extends JpaRepository<PromotionPlan, Long> {

    PromotionPlan findPromotionPlanByName(String name);
}
